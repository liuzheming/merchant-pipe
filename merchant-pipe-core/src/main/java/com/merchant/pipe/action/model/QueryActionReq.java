package com.merchant.pipe.action.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description:
 * <p>
 *
 * @author lzm
 * @date 2023/9/15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueryActionReq {
    private Long pipeId;
    private Long taskId;
    private Long processId;
    private String taskCode;
    private String actionName;
    private String actionNo;
    private List<String> status;
    private String adminAccount;
}
