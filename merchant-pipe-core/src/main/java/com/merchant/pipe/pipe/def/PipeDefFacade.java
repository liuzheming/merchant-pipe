package com.merchant.pipe.pipe.def;

import com.merchant.kernel.common.exception.ServiceException;
import com.merchant.kernel.common.response.PageResult;
import com.merchant.kernel.common.utils.BeanCopyUtils;
import com.merchant.kernel.common.utils.JsonUtils;
import com.merchant.kernel.pipe.db.tables.pojos.ProcessActionPipeDef;
import com.merchant.kernel.pipe.facade.IPipeDefFacade;
import com.merchant.kernel.pipe.facade.model.CreatePipeReq;
import com.merchant.kernel.pipe.facade.model.EditPipeReq;
import com.merchant.kernel.pipe.stage.StageDef;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Create on 2023/4/28
 */
@Service
public class PipeDefFacade implements IPipeDefFacade {

    @Resource
    private PipeDefRepo pipeDefRepo;

    public boolean exist(String procCode, String taskCode) {
        ProcessActionPipeDef pipe = pipeDefRepo.getByTask(procCode, taskCode);
        return pipe != null;
    }

    public PipeDef getByTask(String procCode, String taskCode) {
        ProcessActionPipeDef pipe = pipeDefRepo.getByTask(procCode, taskCode);
        if (null == pipe) {
            throw new ServiceException(
                String.format("pipeDef not fount for procCode:%s, taskCode:%s", procCode, taskCode));
        }
        return convert(pipe);
    }

    public PipeDef getByCode(String pipeDefCode) {
        ProcessActionPipeDef pipeDefEntity = pipeDefRepo.getByCode(pipeDefCode);
        if (null == pipeDefEntity) {
            throw new ServiceException(String.format("pipeDef not found, pipeDefCode:%s", pipeDefCode));
        }
        return convert(pipeDefEntity);
    }

    public static PipeDef convert(ProcessActionPipeDef pipeDefEntity) {
        String stageDef = pipeDefEntity.getStageDef();
        List<StageDef> stageDefs = JsonUtils.str2List(stageDef, StageDef.class);
        if (stageDefs == null) {
            stageDefs = new ArrayList<>();
        }
        PipeDef pipeDef = BeanCopyUtils.build(pipeDefEntity, PipeDef.class, true);
        pipeDef.setStageDefs(stageDefs);
        return pipeDef;
    }

    public PageResult<PipeDef> page(String pipeDefName, Integer pageNo, Integer pageSize) {
        Integer count = pipeDefRepo.count(pipeDefName);
        List<ProcessActionPipeDef> list = pipeDefRepo.list(pipeDefName, pageNo, pageSize);
        List<PipeDef> defs = new ArrayList<>();
        for (ProcessActionPipeDef pipeDefEntity : list) {
            defs.add(convert(pipeDefEntity));
        }
        PageResult<PipeDef> pageResult = new PageResult<>();
        pageResult.setList(defs);
        pageResult.setTotal(count);
        return pageResult;
    }

    public void create(CreatePipeReq createPipeReq) {
        ProcessActionPipeDef pipeDefEntity
            = BeanCopyUtils.build(createPipeReq, ProcessActionPipeDef.class);
        pipeDefRepo.insert(pipeDefEntity);
    }

    public void edit(EditPipeReq editPipeReq) {
        ProcessActionPipeDef pipeDefEntity
            = BeanCopyUtils.build(editPipeReq, ProcessActionPipeDef.class);
        pipeDefRepo.update(pipeDefEntity);
    }

    public int delete(Long pipeDefId) {
        return pipeDefRepo.delete(pipeDefId);
    }

}
