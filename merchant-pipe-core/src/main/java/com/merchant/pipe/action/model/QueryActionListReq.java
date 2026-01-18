package com.merchant.pipe.action.model;

import com.merchant.pipe.action.enums.ProcessActionDefGroupEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


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
public class QueryActionListReq {
    /**
     * 城市编码
     */
    private String cityCode;
    /**
     * 流程定义编码
     */
    private String bizProcCode;

    /**
     * 任务包编码
     *
     * @see ProcessActionDefGroupEnum
     */
    private Integer actionGroupCode;

    /**
     * 品牌编码
     */
    private String brandNo;

    /**
     * 业务编码
     * 0- 内渠，1- 区代
     */
    private Integer bizType = 0;
}
