package com.ke.merchant.kernel.pipe.action;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Create on 2023/4/30
 */
@Data
public class ActionResult<T> {

    private ActionResult() {

    }

    public ActionResult(Boolean success, ObjectNode output, T result, String bizExplain) {
        this.success = success;
        this.output = output;
        this.data = result;
        this.bizExplain = bizExplain;
    }

    public ActionResult(Boolean success, ObjectNode output, T result) {
        this.success = success;
        this.output = output;
        this.data = result;
    }

    public ActionResult(boolean success) {
        this.success = success;
    }

    private Boolean success;
    private ObjectNode output;
    private T data;
    private String bizExplain;

}
