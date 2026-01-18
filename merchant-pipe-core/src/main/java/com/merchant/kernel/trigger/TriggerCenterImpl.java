package com.merchant.kernel.trigger;

import static com.merchant.kernel.trigger.TriggerEventType.ACTION_START_EVENT;
import static com.merchant.kernel.trigger.TriggerEventType.PIPE_START_EVENT;
import static com.merchant.kernel.trigger.TriggerEventType.STAGE_START_EVENT;

import com.merchant.kernel.common.exception.ServiceException;
import com.merchant.kernel.common.utils.NetworkUtil;
import com.merchant.kernel.common.utils.RedisKeyUtils;
import com.merchant.kernel.common.utils.ShutdownUtil;
import com.merchant.kernel.pipe.facade.IActionFacade;
import com.merchant.kernel.pipe.facade.IPipeFacade;
import com.merchant.kernel.pipe.facade.IStageFacade;
import com.merchant.kernel.pipe.pipe.Result;
import java.net.InetAddress;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.redisson.client.protocol.ScoredEntry;
import org.redisson.codec.JsonJacksonCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Create on 2022/3/9
 */
@Slf4j
@Service
public class TriggerCenterImpl implements ITriggerCenter, ApplicationListener<ContextClosedEvent> {

    @Autowired
    private RedissonClient redissonClient;

    private ScheduledThreadPoolExecutor schedule = new ScheduledThreadPoolExecutor(
        1,
        new CustomizableThreadFactory("trigger-schedule-thread"),
        new ThreadPoolExecutor.CallerRunsPolicy()
    );

    @Value("${trigger.disable.labels:default}")
    private List<String> disabledLabels;
    @Value("${pipe.trigger.autoSchedule:false}")
    private Boolean autoSchedule;
    @Value("${pipe.trigger.markServerAddress:false}")
    private Boolean markServerAddress;
    // 两天 2 * 24 * 60 * 60 = 172800
    @Value("${pipe.trigger.timeoutSeconds:172800}")
    private Integer timeoutHours;
    @Resource
    private IPipeFacade pipeFacade;
    @Resource
    private IStageFacade stageFacade;
    @Resource
    private IActionFacade actionFacade;

    RScoredSortedSet<Trigger> zSet;
    private static final String TRIGGER_REGISTRY = "trigger_registry";
    private static final String DELETED = "DELETED";

    @PostConstruct
    public void init() {
        disabledLabels = new java.util.ArrayList<>(disabledLabels);
        disabledLabels.add(DELETED);
        zSet = redissonClient.getScoredSortedSet(RedisKeyUtils.getKey(TRIGGER_REGISTRY), JsonJacksonCodec.INSTANCE);
        LOGGER.info("start init trigger schedule");
        schedule.scheduleAtFixedRate(() -> {
            try {
                scanAndLaunch();
            } catch (Exception e) {
                LOGGER.error("scanAndLaunch exception", e);
            }
        }, 90, 5, TimeUnit.SECONDS);
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        LOGGER.info("Shutting down triggerCenter schedule");
        ShutdownUtil.shutdownThreadPool(schedule, "triggerCenterSchedule");
    }

    @Override
    public void register(LocalDateTime launchTime, String eventType, String relationId,
                         String label, String payload) {
        if (launchTime == null) {
            throw new ServiceException("launchTime can not be null");
        }
        LOGGER.info("register trigger start, eventType:{}, relationId:{}, launchTime:{}",
            eventType, relationId, launchTime);
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCommit() {
                    registerTrigger(launchTime, eventType, relationId, label, payload);
                }
            });
        } else {
            registerTrigger(launchTime, eventType, relationId, label, payload);
        }
        LOGGER.info("register trigger end, eventType:{}, relationId:{}, launchTime:{}",
            eventType, relationId, launchTime);
    }

    private String registerTrigger(LocalDateTime launchTime, String eventType,
                                   String relationId, String label, String payload) {
        LOGGER.info("doRegister trigger, eventType:{}, relationId:{}, launchTime:{}",
            eventType, relationId, launchTime);
        Trigger trigger = new Trigger();
        trigger.setUuid(UUID.randomUUID().toString().replace("-", ""));
        trigger.setLaunchTime(launchTime);
        trigger.setLabel(label);
        trigger.setEventType(eventType);
        trigger.setRelationId(relationId);
        trigger.setPayload(payload);
        if (markServerAddress) {
            InetAddress localAddress = NetworkUtil.getLocalHostExactAddress();
            LOGGER.info("mark trigger address as:{}", localAddress);
            if (null != localAddress) {
                trigger.setServerAddress(localAddress.getHostAddress());
            }
        }
        zSet.add(launchTime.toInstant(ZoneOffset.ofHours(8)).toEpochMilli(), trigger);
        LOGGER.info("register event trigger success, trigger:{}", trigger);
        return trigger.getUuid();
    }

    public boolean cancelTrigger(String uuid) {
        String key = RedisKeyUtils.getKey("lock", TRIGGER_REGISTRY);
        RLock lock = redissonClient.getLock(key);
        boolean succ = false;
        try {
            if (lock.tryLock(5, 10, TimeUnit.SECONDS)) {
                succ = delete(uuid);
            } else {
                throw new TriggerException("cancelTrigger 获取锁失败，uuid:" + uuid);
            }
        } catch (InterruptedException e) {
            LOGGER.error("cancelTrigger Interrupted!", e);
            // Restore interrupted state...
            Thread.currentThread().interrupt();
        } finally {
            if (lock.isHeldByCurrentThread() && lock.isLocked()) {
                lock.unlock();
            }
        }
        return succ;
    }

    public boolean cancelTrigger(String eventType, String relationId) {
        if (StringUtils.isEmpty(eventType) || StringUtils.isEmpty(relationId)) {
            return false;
        }
        for (Trigger trigger : zSet) {
            if (eventType.equals(trigger.getEventType()) && relationId.equals(trigger.getRelationId())) {
                cancelTrigger(trigger.getUuid());
                return true;
            }
        }
        return false;
    }


    public boolean launchByUuid(String uuid) {
        if (StringUtils.isBlank(uuid)) {
            throw new ServiceException("uuid can not be empty");
        }
        Trigger trigger = getTrigger(uuid);
        launch(trigger);
        return true;
    }

    private boolean delete(String uuid) {
        if (StringUtils.isBlank(uuid)) {
            throw new ServiceException("uuid can not be empty");
        }
        return delete(getTrigger(uuid));
    }

    private Trigger getTrigger(String uuid) {
        for (ScoredEntry<Trigger> entry : zSet.entryRange(0, -1)) {
            Trigger trigger = entry.getValue();
            if (uuid.equals(trigger.getUuid())) {
                return trigger;
            }
        }
        return null;
    }

    /**
     * 每隔 2 分钟扫一次，并 launch 所有 launchTime 小于当前时刻的 trigger
     */
    public void scanAndLaunch() {
        if (!autoSchedule) {
            return;
        }
        LOGGER.debug("start scan trigger... ");
        String key = RedisKeyUtils.getKey("lock", TRIGGER_REGISTRY);
        RLock lock = redissonClient.getLock(key);
        try {
            if (!lock.tryLock(30, 10 * 60, TimeUnit.SECONDS)) {
                LOGGER.debug("Lock occupied by other thread, will return... ");
                return;
            }
            Collection<Trigger> triggers = zSet.valueRange(0, true, System.currentTimeMillis(), true);
            if (CollectionUtils.isEmpty(triggers)) {
                LOGGER.debug("Scan end, no trigger should be launched in queue ...");
                return;
            }
            Map<String, String> triggerToRelationId = triggers.stream()
                                                              .collect(Collectors.toMap(Trigger::getUuid, Trigger::getRelationId));
            LOGGER.debug("Detected some trigger, will try launch them, triggers:{} ", triggerToRelationId);
            for (Trigger trigger : triggers) {
                // 超时 trigger 逻辑删除
                if (checkTimeout(trigger)) {
                    LOGGER.error("trigger timeout, will be deleted, trigger:{}", trigger);
                    delete(trigger);
                    continue;
                }
                launch(trigger);
            }
        } catch (InterruptedException e) {
            LOGGER.error("scanAndLaunch Interrupted!", e);
            // Restore interrupted state...
            Thread.currentThread().interrupt();
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }

    }

    private boolean checkTimeout(Trigger trigger) {
        Duration duration = Duration.between(trigger.getLaunchTime(), LocalDateTime.now());
        return !duration.isNegative() && (duration.compareTo(Duration.ofSeconds(timeoutHours)) > 0);
    }

    private boolean launch(Trigger trigger) {
        if (!shouldLaunch(trigger)) {
            return false;
        }
        try {
            LOGGER.info("trigger launching ... , uuid:{}", trigger.getUuid());
            boolean success = doLaunch(trigger);
            // 业务未成功，仍需继续重试，不删除 trigger
            if (success) {
                physicalDelete(trigger);
            } else {
                return false;
            }
        } catch (Exception e) {
            // 报错则删除 trigger
            LOGGER.error("launch fail!, trigger:" + trigger, e);
            delete(trigger);
        }

        return true;
    }

    private boolean shouldLaunch(Trigger trigger) {
        if (disabledLabels.contains(trigger.getLabel())) {
            LOGGER.info("The trigger was prohibited from launch, trigger:{}", trigger);
            return false;
        }

        // 判断触发时间是否已过
        if (trigger.getLaunchTime().isAfter(LocalDateTime.now())) {
            LOGGER.info("not trigger need to launch, it is not time yet ...");
            return false;
        }

        // 判断当前机器是否要处理 trigger
        if (StringUtils.isNotEmpty(trigger.getServerAddress())) {
            // 获取不到本地 ip，不消费
            if (null == NetworkUtil.getLocalHostExactAddress()) {
                return false;
            }
            InetAddress localAddress = NetworkUtil.getLocalHostExactAddress();
            LOGGER.debug("trigger has serverAddress mark, triggerServerAddress:{}, localAddress:{}",
                trigger.getServerAddress(), localAddress.getHostAddress());
            // 本地 ip 与 trigger 标记的 server address 不符，不消费
            if (!trigger.getServerAddress().equals(localAddress.getHostAddress())) {
                return false;
            }
        }
        return true;
    }

    private boolean doLaunch(Trigger trigger) {

        if (null == trigger) {
            throw new ServiceException("trigger can not be null");
        }

        Result result = new Result(false);
        switch (trigger.getEventType()) {
            case PIPE_START_EVENT:
                result = pipeFacade.startPipe(Long.valueOf(trigger.getRelationId()));
                break;
            case STAGE_START_EVENT:
                result = stageFacade.startStage(trigger.getRelationId());
                break;
            case ACTION_START_EVENT:
                result = actionFacade.startAction(trigger.getRelationId());
                break;
            default:
                LOGGER.error("unknown triggerEvent, result:{}, uuid:{}", result, trigger.getUuid());
        }
        LOGGER.info("trigger launched, result:{}, uuid:{}", result, trigger.getUuid());
        return result.getSuccess();
    }

    private boolean physicalDelete(Trigger trigger) {
        return zSet.remove(trigger);
    }

    private boolean delete(Trigger trigger) {
        boolean removed = zSet.remove(trigger);
        trigger.setLabel(DELETED);
        Long score = LocalDateTime.of(2300, 1, 1, 0, 0)
            .toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
        boolean added = zSet.add(score, trigger);
        return removed & added;
    }

}
