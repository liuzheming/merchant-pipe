package com.merchant.kernel.rpc.sdk;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component

public class SeqGenerateRpc {
    private final AtomicLong counter = new AtomicLong(System.currentTimeMillis());

    public String generate() {
        return String.valueOf(counter.incrementAndGet());
    }
}
