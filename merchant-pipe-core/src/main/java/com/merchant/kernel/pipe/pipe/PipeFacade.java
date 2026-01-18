package com.merchant.kernel.pipe.pipe;

import com.merchant.kernel.common.exception.ServiceException;
import com.merchant.kernel.common.utils.MsgBuilder;
import com.merchant.kernel.pipe.action.ActionService;
import com.merchant.kernel.pipe.action.model.QueryActionListReq;
import com.merchant.kernel.pipe.facade.IPipeDefFacade;
import com.merchant.kernel.pipe.facade.IPipeFacade;
import com.merchant.kernel.pipe.pipe.def.PipeDef;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class PipeFacade implements IPipeFacade {

    @Resource
    private IPipeFactory pipeFactory;
    @Resource
    private IPipeDefFacade pipeDefFacade;
    @Resource
    private ActionService actionService;

    @Override
    public PipeDTO create(String pipeDefCode, PipeContext pipeContext, LocalDateTime triggerTime) throws Exception {
        PipeDef pipeDef = pipeDefFacade.getByCode(pipeDefCode);
        if (pipeDef == null) {
            throw new ServiceException(MsgBuilder.build("pipeDef not found, code:{}", pipeDefCode));
        }
        IPipe pipe = pipeFactory.createPipe(pipeDef, pipeContext, triggerTime);
        return PipeDTO.of(pipe);
    }

    @Override
    public PipeDTO create4CustomTask(String pipeDefCode, PipeContext pipeContext) throws Exception {
        PipeDef pipeDef = pipeDefFacade.getByCode(pipeDefCode);
        if (pipeDef == null) {
            throw new ServiceException(MsgBuilder.build("pipeDef not found, code:{}", pipeDefCode));
        }
        IPipe pipe = pipeFactory.createPipe(pipeDef, pipeContext, null);
        return PipeDTO.of(pipe);
    }

    @Override
    public PipeDTO create4CustomTask(QueryActionListReq queryActionListReq, PipeContext pipeContext) throws Exception {
        throw new ServiceException("custom task pipe creation is not supported in merchant-pipe");
    }

    @Override
    public PipeDTO createPipe4TaskCode(PipeContext pipeContext, LocalDateTime triggerTime) throws Exception {
        PipeDef pipeDef = pipeDefFacade.getByTask(pipeContext.getProcCode(), pipeContext.getTaskCode());
        if (null == pipeDef) {
            String msg = MsgBuilder.build("pipeDef not found, procCode:{}, taskCode:{}",
                pipeContext.getProcCode(), pipeContext.getTaskCode());
            throw new ServiceException(msg);
        }
        IPipe pipe = pipeFactory.createPipe(pipeDef, pipeContext, triggerTime);
        return PipeDTO.of(pipe);
    }

    @Override
    public PipeDTO get(Long pipeId) {
        IPipe pipe = pipeFactory.get(pipeId);
        if (null == pipe) {
            return null;
        }
        return PipeDTO.of(pipe);
    }

    @Override
    public List<PipeDTO> listByTaskId(Long taskId) {
        List<PipeDTO> list = new ArrayList<>();
        List<IPipe> pipes = pipeFactory.listByTaskId(taskId);
        for (IPipe pipe : pipes) {
            list.add(PipeDTO.of(pipe));
        }
        return list;
    }

    @Transactional(rollbackFor = Exception.class)
    public Result startPipe(Long pipeId) {
        IPipe pipe = pipeFactory.get(pipeId);
        if (null == pipe) {
            return new Result(false);
        }
        return pipe.startup();
    }

    @Transactional(rollbackFor = Exception.class)
    public Result startPipeImmediately(Long pipeId) {
        IPipe pipe = pipeFactory.get(pipeId);
        if (null == pipe) {
            return new Result(false);
        }
        return pipe.startupImmediately();
    }

    @Override
    public void setPipeOrganizer(Long pipeId, List<String> pipeOrganizerList) {
        pipeFactory.setPipeOrganizer(pipeId, pipeOrganizerList);
    }

    @Transactional(rollbackFor = Exception.class)
    public Result restartPipe(Long pipeId) {
        IPipe pipe = pipeFactory.get(pipeId);
        if (null == pipe) {
            return new Result(false);
        }
        return pipe.restart();
    }
}
