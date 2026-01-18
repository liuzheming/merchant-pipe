package com.ke.merchant.kernel.pipe.facade;

import com.ke.merchant.kernel.pipe.pipe.Result;
import com.ke.merchant.kernel.pipe.stage.StageDTO;

import java.util.List;

/**
 * Create on 2023/5/4
 */
public interface IStageFacade {

    Result startStage(String stageNo);

    List<StageDTO> list(Long pipeId);

}
