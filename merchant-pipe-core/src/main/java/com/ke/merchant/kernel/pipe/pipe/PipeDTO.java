package com.merchant.pipe.pipe;

import com.merchant.common.utils.BeanCopyUtils;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Create on 2023/5/4
 */
@Data
public class PipeDTO {

    private Long id;
    private String name;
    private Long taskId;
    private Long processId;
    private PipeContext pipeContext;
    private String headStageNo;
    private String tailStageNo;
    private String status;
    private LocalDateTime triggerTime;

    private String pipeOrganizer;

    private LocalDateTime doneTime;

    public static PipeDTO of(IPipe pipe) {
        PipeDTO pipeDTO = BeanCopyUtils.build(pipe, PipeDTO.class, true);
        pipeDTO.setStatus(pipe.status().status);
        return pipeDTO;
    }

}
