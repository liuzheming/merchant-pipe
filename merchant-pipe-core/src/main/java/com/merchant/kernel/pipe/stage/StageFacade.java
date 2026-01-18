package com.merchant.kernel.pipe.stage;

import com.merchant.kernel.common.utils.BeanCopyUtils;
import com.merchant.kernel.pipe.facade.IActionFacade;
import com.merchant.kernel.pipe.facade.IStageFacade;
import com.merchant.kernel.pipe.pipe.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Create on 2023/4/28
 */
@Slf4j
@Component
public class StageFacade implements IStageFacade {

    @Resource
    private IStageFactory stageFactory;
    @Resource
    private IActionFacade actionFacade;
    @Resource
    private StageRepo stageRepo;

    @Transactional(rollbackFor = Exception.class)
    public Result startStage(String stageNo) {
        Stage stage = stageFactory.get(stageNo);
        if (null == stage) {
            return new Result(false);
        }
        return stage.startup();
    }

    @Override
    public List<StageDTO> list(Long pipeId) {
        List<Stage> stages = stageRepo.list(pipeId);
        List<StageDTO> stageDTOs = new ArrayList<>();
        if (CollectionUtils.isEmpty(stages)) {
            return new ArrayList<>();
        }
        for (Stage stage : stages) {
            StageDTO stageDTO = BeanCopyUtils.build(stage, StageDTO.class);
            stageDTO.setActions(actionFacade.listByStageNo(stageDTO.getStageNo()));
            stageDTOs.add(stageDTO);
        }
        return stageDTOs;
    }

}
