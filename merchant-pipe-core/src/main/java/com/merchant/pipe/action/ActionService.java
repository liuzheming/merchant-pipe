package com.merchant.pipe.action;

import com.merchant.kernel.common.constant.UserInfo;
import com.merchant.kernel.common.exception.ServiceException;
import com.merchant.kernel.common.utils.RedisKeyUtils;
import com.merchant.kernel.common.utils.UserManager;
import com.merchant.kernel.enums.ExecModeEnum;
import com.merchant.kernel.pipe.ActionException;
import com.merchant.kernel.pipe.ActionSkipException;
import com.merchant.kernel.pipe.pipe.BuildResult;
import com.merchant.kernel.pipe.pipe.Result;
import com.merchant.kernel.pipe.stage.Stage;
import com.merchant.kernel.pipe.stage.StageFactory;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

import static com.merchant.kernel.pipe.ActionStatusEnum.*;

/**
 * Minimal action runtime service.
 */
@Slf4j
@Component
public class ActionService implements IActionService {

    public static final String LOCK_NAME = "actions.run";

    @Resource
    private RedissonClient redissonClient;
    @Resource
    private StageFactory stageFactory;
    @Resource
    private IActionFactory actionFactory;

    @Override
    public void resetAction(String itemNo) {
        resetAction(itemNo, null);
    }

    @Override
    public void resetAction(String itemNo, ExecModeEnum mode) {
        UserInfo user = UserManager.getUser() == null ? UserManager.getDefaultUser() : UserManager.getUser();
        LOGGER.info("reset action, itemNo:{}, user:{}", itemNo, user.getName());
    }

    public Result run(String itemNo) {
        ActionResult<?> actionResult = doRun(itemNo);
        return new Result(actionResult.getSuccess());
    }

    public ActionResult<?> doRun(String itemNo) {
        String lockKey = RedisKeyUtils.getKey(LOCK_NAME, itemNo);
        RLock rLock = redissonClient.getLock(lockKey);
        boolean locked;
        ActionResult<?> result = new ActionResult<>(false);
        try {
            locked = rLock.tryLock(0, 300, TimeUnit.SECONDS);
            if (!locked) {
                throw new ServiceException("get lock failed, actionNo:" + itemNo);
            }

            AbsAction action = (AbsAction) actionFactory.loadAction(itemNo);
            if (action == null) {
                return result;
            }
            if (action.getStatus() == EXEC_SUCC) {
                return result;
            }
            if (action.getStatus() == EXEC_SKIP) {
                Stage stage = stageFactory.get(action.getStageNo());
                if (null != stage) {
                    ActionContext context = action.getContext();
                    stage.signal(itemNo, context == null ? null : context.getOutput());
                }
                return result;
            }
            int execCount = action.getExecCount() == null ? 0 : action.getExecCount();
            if ((action.getStatus() != INIT_SUCC && action.getStatus() != WAIT_EXEC)
                && !(execCount == 1 && action.getStatus() == EXEC_FAIL)) {
                throw new ActionException("Action status not executable, itemNo:" + itemNo + ", status=" + action.getStatus());
            }

            switch (action.getExecMode()) {
                case SKIP:
                    result = action.skip();
                    break;
                case MANUAL:
                    if (action.getStatus().equals(WAIT_EXEC)) {
                        BuildResult buildResult = action.build();
                        if (buildResult.getSkip()) {
                            result = action.skip();
                        } else {
                            result = action.execute();
                        }
                    }
                    break;
                case AUTO:
                    BuildResult buildResult = action.build();
                    if (buildResult.getSkip()) {
                        result = action.skip();
                    } else {
                        result = action.execute();
                    }
                    break;
                default:
                    throw new ActionException("unrecognized actionMode, action:" + itemNo);
            }

            if (result.getSuccess()) {
                Stage stage = stageFactory.get(action.getStageNo());
                if (null != stage) {
                    ActionContext context = action.getContext();
                    stage.signal(itemNo, context == null ? null : context.getOutput());
                }
            }

        } catch (ActionSkipException e) {
            return result;
        } catch (InterruptedException e) {
            LOGGER.error("action run interrupted", e);
            Thread.currentThread().interrupt();
        } finally {
            if (rLock.isHeldByCurrentThread() && rLock.isLocked()) {
                rLock.unlock();
            }
        }
        return result;
    }
}
