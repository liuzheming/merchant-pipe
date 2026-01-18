package com.merchant.kernel.pipe.simulator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.merchant.kernel.pipe.action.AbsAction;
import com.merchant.kernel.pipe.action.ActionResult;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * Create on 2022/2/19
 */
@Slf4j
public class SampleExecOkAction extends AbsAction<Map<String, Object>, Map<String, Object>> {

    public SampleExecOkAction() {
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

        ObjectNode output = new ObjectMapper().createObjectNode();
        for (Map.Entry<String, Object> entry : p.entrySet()) {
            output.put(entry.getKey(), String.valueOf(entry.getValue()));
        }
        ActionResult<Map<String, Object>> actionResult = new ActionResult<>(true, output, new HashMap<>());
        actionResult.setBizExplain("hello，Are U OK ?");
        return actionResult;
    }


}
