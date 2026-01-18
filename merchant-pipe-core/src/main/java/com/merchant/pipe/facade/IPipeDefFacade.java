package com.merchant.pipe.facade;

import com.merchant.common.response.PageResult;
import com.merchant.pipe.facade.model.CreatePipeReq;
import com.merchant.pipe.facade.model.EditPipeReq;
import com.merchant.pipe.pipe.def.PipeDef;

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
