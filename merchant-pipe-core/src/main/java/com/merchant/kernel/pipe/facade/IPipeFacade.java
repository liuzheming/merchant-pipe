package com.merchant.kernel.pipe.facade;

import com.merchant.kernel.pipe.action.model.QueryActionListReq;
import com.merchant.kernel.pipe.pipe.PipeContext;
import com.merchant.kernel.pipe.pipe.PipeDTO;
import com.merchant.kernel.pipe.pipe.Result;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Create on 2023/5/4
 */
public interface IPipeFacade {

    /**
     * 创建 pipe
     *
     * @param pipeDefCode pipe定义code
     * @param pipeContext pipe上下文
     * @param triggerTime 触发时间, 如果为空, 则不会自动被触发，需要客户端主动来触发
     * @return pipeDTO 创建出来的 pipe 对象
     */
    PipeDTO create(String pipeDefCode, PipeContext pipeContext, LocalDateTime triggerTime)
        throws Exception;


    PipeDTO create4CustomTask(String pipeDefCode, PipeContext pipeContext)
        throws Exception;


    PipeDTO create4CustomTask(QueryActionListReq queryActionListReq, PipeContext pipeContext)
        throws Exception;

    PipeDTO createPipe4TaskCode(PipeContext pipeContext, LocalDateTime triggerTime)
        throws Exception;

    PipeDTO get(Long pipeId);

    List<PipeDTO> listByTaskId(Long taskId);


    Result startPipe(Long pipeId);


    /**
     * 设置pipe的组织者
     * @param pipeId
     */
    void setPipeOrganizer(Long pipeId, List<String> pipeOrganizerList);



}
