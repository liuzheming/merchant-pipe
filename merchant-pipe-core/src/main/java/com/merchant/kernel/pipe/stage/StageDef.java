package com.merchant.kernel.pipe.stage;

import com.merchant.kernel.pipe.action.ActionDef;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Stage 之中的多个 action 是并行关系，
 * 也就是说，stage 中的第一个 action 如果执行失败了，那么第二个 action 还是会被触发
 */
@Data
public class StageDef {

    public StageDef() {

    }

    public StageDef(List<ActionDef> actionDefs) {
        this.actionDefs = actionDefs;
    }

    List<ActionDef> actionDefs = new ArrayList<>();

}
