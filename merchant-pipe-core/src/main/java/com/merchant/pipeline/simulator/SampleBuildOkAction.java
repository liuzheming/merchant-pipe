package com.merchant.pipeline.simulator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.merchant.kernel.common.exception.ServiceException;
import com.merchant.kernel.pipe.ActionStuckException;
import com.merchant.kernel.pipe.action.AbsAction;
import com.merchant.kernel.pipe.action.ActionResult;
import com.merchant.kernel.pipe.facade.IActionFacade;
import org.jooq.tools.StringUtils;

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
public class SampleBuildOkAction extends AbsAction<Map<String, String>, Map<String, String>> {

    private final static String buildResult = "Build OK!";
    @Resource
    private IActionFacade actionFacade;
    protected Map<String, String> buildParam() {
        HashMap<String, String> map = new HashMap<>();
        map.put("key", buildResult);
        return map;
    }


    @Override
    protected ActionResult<Map<String, String>> doExecute(Map<String, String> param) throws ActionStuckException {
        String msg = param.get("key");

        // 验证 execute 方法的入参中可以正确获取到 buildParam 的结果
        if (StringUtils.isEmpty(msg) || !buildResult.equals(msg)) {
            throw new ServiceException("SampleBuildOkAction test fail, param delivery error!");
        }
        return new ActionResult<>(true, null, new HashMap<>());
    }

    @Override
    protected TypeReference<Map<String, String>> paramType() {
        return new TypeReference<Map<String, String>>(){};
    }
}
