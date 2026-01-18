package com.ke.merchant.kernel.pipe.simulator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ke.merchant.kernel.common.exception.ServiceException;
import com.ke.merchant.kernel.pipe.action.AbsAction;
import com.ke.merchant.kernel.pipe.action.ActionResult;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/**
 * Create on 2022/2/19
 */
@Slf4j
public class SampleExecExAction extends AbsAction<Map<String, Object>, Map<String, Object>> {

    public SampleExecExAction() {
        super();
    }

    @Override
    protected Map<String, Object> buildParam() {
        return null;
    }

    @Override
    protected TypeReference<Map<String, Object>> paramType() {
        return new TypeReference<Map<String, Object>>() {
        };
    }

    @Override
    protected ActionResult<Map<String, Object>> doExecute(Map<String, Object> p) {
        LOGGER.info("--------- doExecute start, wait to update action, action:{}", this);


        throw new ServiceException("门店联网时间无效");
    }


}
