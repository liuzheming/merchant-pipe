package com.merchant.pipeline.action.dto;

import com.merchant.kernel.common.utils.DateUtil;
import com.merchant.kernel.pipe.db.tables.pojos.ProcessAction;
import com.merchant.kernel.pipe.ActionStatusEnum;

public class ActionDTO {
    private Long id;
    private String itemNo;
    private String name;
    private String status;
    private String statusDesc;
    private Long pipeId;
    private String errMsg;
    private Integer execCount;
    private String doneTime;
    private String createTime;

    public static ActionDTO of(ProcessAction entity) {
        if (entity == null) {
            return null;
        }
        ActionDTO dto = new ActionDTO();
        dto.id = entity.getId();
        dto.itemNo = entity.getItemNo();
        dto.name = entity.getName();
        dto.pipeId = entity.getPipeId();
        if (entity.getStatus() != null) {
            ActionStatusEnum statusEnum = ActionStatusEnum.of(entity.getStatus());
            if (statusEnum != null) {
                dto.status = statusEnum.status;
                dto.statusDesc = statusEnum.desc;
            } else {
                dto.status = entity.getStatus();
            }
        }
        dto.errMsg = entity.getErrMsg();
        dto.execCount = entity.getExecCount();
        if (entity.getDoneTime() != null) {
            dto.doneTime = DateUtil.localDateTimeToString(entity.getDoneTime());
        }
        if (entity.getCtime() != null) {
            dto.createTime = DateUtil.localDateTimeToString(entity.getCtime());
        }
        return dto;
    }

    public Long getId() {
        return id;
    }

    public String getItemNo() {
        return itemNo;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public String getStatusDesc() {
        return statusDesc;
    }

    public Long getPipeId() {
        return pipeId;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public Integer getExecCount() {
        return execCount;
    }

    public String getDoneTime() {
        return doneTime;
    }

    public String getCreateTime() {
        return createTime;
    }
}
