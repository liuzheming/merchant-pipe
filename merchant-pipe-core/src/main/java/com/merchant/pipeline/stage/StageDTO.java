package com.merchant.pipeline.stage;

import com.merchant.kernel.pipe.action.dto.ActionDTO;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Create on 2023/5/1
 */
@Slf4j
@Data
public class StageDTO {

    private Long id;

    private String stageNo;

    private String nextStageNo;

    private LocalDateTime doneTime;

    private Long pipeId;

    private String status;

    private List<ActionDTO> actions;

}
