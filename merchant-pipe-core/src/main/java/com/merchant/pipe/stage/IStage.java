package com.merchant.pipe.stage;

/**
 * Create on 2023/5/1
 */
public interface IStage {

    Long getId();

    String getStageNo();

    String getNextStageNo();

    String getStatus();

}
