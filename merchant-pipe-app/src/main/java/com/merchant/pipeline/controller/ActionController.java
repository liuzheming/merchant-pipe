package com.merchant.pipeline.controller;

import com.merchant.kernel.common.response.ResponseResult;
import com.merchant.kernel.pipe.action.dto.ActionDTO;
import com.merchant.kernel.pipe.facade.IActionFacade;
import com.merchant.kernel.pipe.pipe.Result;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/actions")
public class ActionController {

    @Resource
    private IActionFacade actionFacade;

    @GetMapping("/{itemNo}")
    public ResponseResult<ActionDTO> get(@PathVariable String itemNo) {
        return ResponseResult.success(actionFacade.get(itemNo));
    }

    @PostMapping("/{itemNo}/start")
    public ResponseResult<Result> start(@PathVariable String itemNo) {
        return ResponseResult.success(actionFacade.startAction(itemNo));
    }
}
