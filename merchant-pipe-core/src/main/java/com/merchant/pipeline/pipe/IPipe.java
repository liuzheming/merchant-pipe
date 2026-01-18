package com.merchant.pipeline.pipe;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Create on 2022/12/4
 */
public interface IPipe extends IPipeLifeCycle {

    Long getId();

    PipeStatusEnum status();

    boolean checkDone();

    PipeContext getPipeContext();

    void refreshContext(String stageNo, ObjectNode actionOutput);

}
