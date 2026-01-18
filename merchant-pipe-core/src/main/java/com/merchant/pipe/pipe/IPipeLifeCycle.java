package com.merchant.pipe.pipe;

import com.merchant.kernel.pipe.pipe.def.PipeDef;

import java.time.LocalDateTime;

/**
 *
 */
public interface IPipeLifeCycle {

    /**
     * 初始化
     */
    void init(String pipeName, PipeDef pipeDef,
              PipeContext pipeContext, LocalDateTime triggerTime) throws Exception;


    void init(PipeDef pipeDef) throws Exception;

    /**
     * 启动
     */
    Result startup();


    /**
     * 启动
     */
    Result startupImmediately();

    /**
     * 结束
     */
    Boolean shutdown();


    /**
     * 重启
     *
     * @return
     */
    Result restart();

    void signal(String stageNo);


    void complete();
}
