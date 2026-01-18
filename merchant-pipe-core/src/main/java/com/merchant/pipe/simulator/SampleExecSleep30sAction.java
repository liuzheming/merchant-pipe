package com.merchant.pipe.simulator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.merchant.kernel.pipe.action.AbsAction;
import com.merchant.kernel.pipe.action.ActionResult;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * Create on 2022/2/19
 */
@Slf4j
public class SampleExecSleep30sAction extends AbsAction<Map<String, Object>, Map<String, Object>> {

    public SampleExecSleep30sAction() {
        super();
    }

    @Override
    protected Map<String, Object> buildParam() {
        Map<String, Object> map = new HashMap<>();
        return map;
    }

    @Override
    protected TypeReference<Map<String, Object>> paramType() {
        return new TypeReference<Map<String, Object>>() {

        };
    }

    @Override
    protected ActionResult<Map<String, Object>> doExecute(Map<String, Object> p) {
        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return new ActionResult<>(true, null, new HashMap<>());
    }


}
