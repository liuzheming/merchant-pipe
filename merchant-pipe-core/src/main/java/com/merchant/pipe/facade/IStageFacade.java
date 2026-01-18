package com.merchant.pipe.facade;

import com.merchant.pipe.pipe.Result;
import com.merchant.pipe.stage.StageDTO;

import java.util.List;

/**
 * Create on 2023/5/4
 */
public interface IStageFacade {

    Result startStage(String stageNo);

    List<StageDTO> list(Long pipeId);

}
