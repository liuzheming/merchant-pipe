package com.merchant.pipe.controller;

import com.merchant.common.response.ResponseResult;
import com.merchant.pipe.action.dto.ActionDTO;
import com.merchant.pipe.facade.IActionFacade;
import com.merchant.pipe.pipe.Result;
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
