package com.merchant.pipe.pipe;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * Create on 2023/4/30
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BuildResult {

    private boolean skip;

    public boolean getSkip() {
        return skip;
    }

    public void setSkip(boolean skip) {
        this.skip = skip;
    }
}
