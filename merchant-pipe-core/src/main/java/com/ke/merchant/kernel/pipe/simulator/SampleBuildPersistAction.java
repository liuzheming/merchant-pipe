package com.merchant.pipe.simulator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.merchant.common.exception.ServiceException;
import com.merchant.common.utils.JsonUtils;
import com.merchant.pipe.ActionStuckException;
import com.merchant.pipe.action.AbsAction;
import com.merchant.pipe.action.ActionRepo;
import com.merchant.pipe.action.ActionResult;
import com.merchant.pipe.db.tables.pojos.ProcessAction;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * Description: 测试 build 方法的结果是否能正确传入到 execute 中去
 * <p>
 *
 * @author lzm
 * @date 2023/7/17
 */
public class SampleBuildPersistAction extends AbsAction<Map<String, String>, Map<String, String>> {

    private final static String buildResult = "Build OK!";
    @Resource
    private ActionRepo actionRepo;
    protected Map<String, String> buildParam() {
        HashMap<String, String> map = new HashMap<>();
        map.put("key", buildResult);
        return map;
    }


    @Override
    protected ActionResult<Map<String, String>> doExecute(Map<String, String> param) throws ActionStuckException {
        String msg = param.get("key");

        // 验证 build 结束之后，已经立即将结果持久化到 DB
        ProcessAction action = actionRepo.get(getItemNo());
        if (action == null || action.getParam() == null) {
            throw new ServiceException("SampleBuildOkAction test fail, param persist error!");
        }
        String persistedValue = JsonUtils.str2JsonNode(action.getParam()).path("key").asText();
        if (!msg.equals(persistedValue)) {
            throw new ServiceException("SampleBuildOkAction test fail, param persist error!");
        }
        return new ActionResult<>(true, null, new HashMap<>());
    }

    @Override
    protected TypeReference<Map<String, String>> paramType() {
        return new TypeReference<Map<String, String>>(){};
    }
}
