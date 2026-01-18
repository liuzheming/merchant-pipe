package com.ke.merchant.kernel.pipe.pipe.def;

import com.ke.merchant.kernel.pipe.stage.StageDef;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * group 之中的多个 stage 是串行关系
 * 也就是说，group 中的第一个 stage 如果执行失败了，那么第二个 stage 是不会被触发的
 * <p>
 * Create on 2022/12/4
 */
@Data
@NoArgsConstructor
public class PipeDef {

    private Long          id;
    private String        name;
    private String        code;
    private String        procCode;
    private Long          procDefId;
    private String        procName;
    private String        taskCode;
    private String        taskName;
    private String        stageDef;
    private LocalDateTime ctime;
    private LocalDateTime mtime;
    private Integer       deleted;

    public PipeDef(String pipeName) {
        this.name = pipeName;
    }

    private List<StageDef> stageDefs = new ArrayList<>();

}
