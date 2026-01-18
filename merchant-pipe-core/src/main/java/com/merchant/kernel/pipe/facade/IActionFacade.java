package com.merchant.kernel.pipe.facade;

import com.merchant.kernel.pipe.action.dto.ActionDTO;
import com.merchant.kernel.pipe.pipe.Result;

import java.util.List;

public interface IActionFacade {

    Result startAction(String actionNo);

    ActionDTO get(String itemNo);

    List<ActionDTO> listByPipeId(Long pipeId);

    List<ActionDTO> listByStageNo(String stageNo);
}
