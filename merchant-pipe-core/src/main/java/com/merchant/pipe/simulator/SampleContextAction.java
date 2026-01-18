package com.merchant.pipe.simulator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.merchant.common.utils.JsonUtils;
import com.merchant.pipe.action.AbsAction;
import com.merchant.pipe.action.ActionResult;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

/**
 * Create on 2022/2/19
 */
@Slf4j
public class SampleContextAction extends AbsAction<Map<String, Object>, Map<String, Object>> {

    public SampleContextAction() {
        super();
    }

    @Override
    protected Map<String, Object> buildParam() {
        assertContext();
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
        assertContext();
        ObjectNode output = JsonUtils.getObjectMapper().createObjectNode();
        output.put("outputTestKey", "outputTestVal");
        return new ActionResult<>(true, output, new HashMap<>());
    }

    private void assertContext() {
        Assert.isNull(this.getContext().getInput().get("notExistKey"));
        Assert.isTrue("".equals(this.getContext().getInput().findPath("notExistKey").asText("")));
        Assert.isTrue("testInputVal".equals(this.getContext().getInput().get("testInputKey").asText()));
        Assert.isTrue("testInputVal".equals(this.getContext().getInput().get("testKey").asText()));
        Assert.isTrue("haha, i am tester".equals(this.getContext().getExtData().get("testKey")));
        Assert.isTrue("11000".equals(this.getContext().getCityCode()));
    }

}
