package com.ke.merchant.kernel.pipe.action.model;

import com.ke.merchant.kernel.pipe.stage.StageDef;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * <p>
 *
 * @author KaerKing
 * @date 2024/6/3
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomTaskDTO {
    /**
     * 任务名称
     */
    private String customTaskName;
    /**
     * 流程id
     */
    private Long processInstId;

    /**
     * 任务实例ID
     */
    private Long taskInstId;

    private List<StageDef> stageDefList = new ArrayList<>();

}
