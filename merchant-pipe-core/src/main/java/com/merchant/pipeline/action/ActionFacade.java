package com.merchant.pipeline.action;

import com.merchant.kernel.pipe.db.tables.pojos.ProcessAction;
import com.merchant.kernel.pipe.facade.IActionFacade;
import com.merchant.kernel.pipe.pipe.Result;
import com.merchant.kernel.pipe.action.dto.ActionDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ActionFacade implements IActionFacade {

    @Resource
    private ActionRepo actionRepo;
    @Resource
    private ActionService actionService;

    @Override
    public ActionDTO get(String itemNo) {
        ProcessAction actionEntity = actionRepo.get(itemNo);
        return ActionDTO.of(actionEntity);
    }

    @Override
    public List<ActionDTO> listByPipeId(Long pipeId) {
        List<ProcessAction> records = actionRepo.listByPipeId(pipeId);
        return records.stream().map(ActionDTO::of).collect(Collectors.toList());
    }

    @Override
    public Result startAction(String actionNo) {
        return actionService.run(actionNo);
    }

    public Result redo(String itemNo) {
        return actionService.run(itemNo);
    }

    @Override
    public List<ActionDTO> listByStageNo(String stageNo) {
        List<ProcessAction> records = actionRepo.listByStageNo(stageNo);
        return records.stream().map(ActionDTO::of).collect(Collectors.toList());
    }
}
