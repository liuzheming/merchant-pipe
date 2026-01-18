package com.merchant.pipe.pipe;

import com.merchant.kernel.pipe.pipe.def.PipeDef;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Create on 2022/12/4
 */
public interface IPipeFactory {

    /**
     * 创建 Pipe
     */
    IPipe createPipe(PipeDef pipeDef,
                     PipeContext pipeContext, LocalDateTime triggerTime)
        throws Exception;

    /**
     * 创建 Pipe for custom task
     */
    IPipe createPipe4CustomTask(PipeDef pipeDef, PipeContext pipeContext)
        throws Exception;

    IPipe get(long pipeId);

    List<IPipe> listByTaskId(long taskId);

    List<IPipe> listNotDone(String status);


    /**
     * 设置pipe的组织者
     */
    void setPipeOrganizer(Long pipeId, List<String> pipeOrganizerList);

}
