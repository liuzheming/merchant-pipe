package com.ke.merchant.kernel.trigger;

import java.time.LocalDateTime;

/**
 * Create on 2022/3/8
 */
public interface ITriggerCenter {

    /**
     * 注册一个回调类型为 Event 的 trigger:
     * TriggerService 将会在指定时间发送一个 Event，Event 包含两个成员变量，triggerKey 和 eventType
     *
     * @param launchTime 发送事件的时间
     * @param eventType  注册方自定义的事件类型 eventType + relationId 需要唯一
     * @param relationId 注册方自定义的关联ID   eventType + relationId 需要唯一
     * @return uuid 返回 trigger 的 uuid
     */
    void register(LocalDateTime launchTime, String eventType, String relationId, String label, String payload);

    /**
     * @param uuid registerEventTrigger 所返回的 uuid
     * @return cancel 是否成功
     */
    boolean cancelTrigger(String uuid);

    /**
     * 入参是注册时使用的 eventType 和 relationId
     */
    boolean cancelTrigger(String eventType, String relationId);

    /**
     * 扫描并触发 launchTime 小于当前时间的 trigger
     */
    void scanAndLaunch();
}
