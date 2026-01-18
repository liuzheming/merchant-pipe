package com.merchant.pipe.controller;

import com.merchant.common.response.ResponseResult;
import com.merchant.pipe.action.dto.ActionDTO;
import com.merchant.pipe.facade.IActionFacade;
import com.merchant.pipe.facade.IPipeFacade;
import com.merchant.pipe.facade.IStageFacade;
import com.merchant.pipe.pipe.PipeContext;
import com.merchant.pipe.pipe.PipeDTO;
import com.merchant.pipe.pipe.PipeFacade;
import com.merchant.pipe.pipe.Result;
import com.merchant.pipe.stage.StageDTO;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/pipes")
public class PipeController {

    @Resource
    private IPipeFacade pipeFacade;
    @Resource
    private IActionFacade actionFacade;
    @Resource
    private IStageFacade stageFacade;

    @PostMapping
    public ResponseResult<PipeDTO> create(@RequestBody CreatePipeRequest request) throws Exception {
        PipeDTO pipeDTO = pipeFacade.create(request.getPipeDefCode(), request.getPipeContext(), request.getTriggerTime());
        return ResponseResult.success(pipeDTO);
    }

    @PostMapping("/{pipeId}/start")
    public ResponseResult<Result> start(@PathVariable Long pipeId) {
        return ResponseResult.success(pipeFacade.startPipe(pipeId));
    }

    @PostMapping("/{pipeId}/start-immediately")
    public ResponseResult<Result> startImmediately(@PathVariable Long pipeId) {
        if (pipeFacade instanceof PipeFacade) {
            Result result = ((PipeFacade) pipeFacade).startPipeImmediately(pipeId);
            return ResponseResult.success(result);
        }
        return ResponseResult.success(pipeFacade.startPipe(pipeId));
    }

    @GetMapping("/{pipeId}")
    public ResponseResult<PipeDTO> get(@PathVariable Long pipeId) {
        return ResponseResult.success(pipeFacade.get(pipeId));
    }

    @GetMapping("/{pipeId}/actions")
    public ResponseResult<List<ActionDTO>> listActions(@PathVariable Long pipeId) {
        return ResponseResult.success(actionFacade.listByPipeId(pipeId));
    }

    @GetMapping("/{pipeId}/stages")
    public ResponseResult<List<StageDTO>> listStages(@PathVariable Long pipeId) {
        return ResponseResult.success(stageFacade.list(pipeId));
    }

    public static class CreatePipeRequest {
        private String pipeDefCode;
        private PipeContext pipeContext;
        private LocalDateTime triggerTime;

        public String getPipeDefCode() {
            return pipeDefCode;
        }

        public void setPipeDefCode(String pipeDefCode) {
            this.pipeDefCode = pipeDefCode;
        }

        public PipeContext getPipeContext() {
            return pipeContext;
        }

        public void setPipeContext(PipeContext pipeContext) {
            this.pipeContext = pipeContext;
        }

        public LocalDateTime getTriggerTime() {
            return triggerTime;
        }

        public void setTriggerTime(LocalDateTime triggerTime) {
            this.triggerTime = triggerTime;
        }
    }
}
