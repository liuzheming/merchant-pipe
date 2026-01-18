package com.merchant.kernel.pipe.simulator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.merchant.kernel.common.exception.ServiceException;
import com.merchant.kernel.pipe.action.AbsAction;
import com.merchant.kernel.pipe.action.ActionResult;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * Create on 2022/2/19
 */
@Slf4j
public class SampleBuildExAction extends AbsAction<Map<String, Object>, Map<String, Object>> {

    public SampleBuildExAction() {
        super();
    }

    @Override
    protected Map<String, Object> buildParam() {
        throw new ServiceException("门店联网时间无效");
    }

    @Override
    protected TypeReference<Map<String, Object>> paramType() {
        return new TypeReference<Map<String, Object>>() {
        };
    }

    @Override
    protected ActionResult<Map<String, Object>> doExecute(Map<String, Object> p) {
        LOGGER.info("--------- doExecute start, wait to update action, action:{}", this);

        LOGGER.info("++++++++++++ doExecute completed, wait to update action, action:{}", this);

//        if (1 == 1) throw new ServiceException("haha");

//        Map<String, Object> param = new HashMap<>();
//        Map<String, Object> result = new HashMap<>();
//        param.put("param1", "paramValue1");
//        result.put("result1", "resultValue1");
//
//        this.param = param;
//        this.result = result;
        return new ActionResult<>(true, null, null);
    }


}
