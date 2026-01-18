package com.merchant.pipeline.simulator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.merchant.kernel.pipe.ActionStuckException;
import com.merchant.kernel.pipe.action.AbsAction;
import com.merchant.kernel.pipe.action.ActionResult;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Create on 2022/2/19
 */
@Slf4j
public class SampleExecStuckAction extends AbsAction<Map<String, Object>, Map<String, Object>> {

    public SampleExecStuckAction() {
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
        if (!LocalDateTime.now().minusMinutes(1000).isAfter(ctime)) {
            throw new ActionStuckException("The time has not come.");
        }
        return new ActionResult<>(true, null, result);
    }


}
