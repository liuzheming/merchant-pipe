package com.merchant.kernel.pipe.facade;

import com.merchant.kernel.common.response.PageResult;
import com.merchant.kernel.pipe.facade.model.CreatePipeReq;
import com.merchant.kernel.pipe.facade.model.EditPipeReq;
import com.merchant.kernel.pipe.pipe.def.PipeDef;

/**
 * Create on 2023/5/4
 */
public interface IPipeDefFacade {

    void create(CreatePipeReq req);

    void edit(EditPipeReq editPipeReq);

    int delete(Long pipeDefId);

    boolean exist(String procCode, String taskCode);

    PipeDef getByTask(String procCode, String taskCode);

    PipeDef getByCode(String pipeDefCode);

    PageResult<PipeDef> page(String pipeDefName, Integer pageNo, Integer pageSize);


}
