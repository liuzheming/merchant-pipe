package com.ke.merchant.kernel.trigger;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Map;

/**
 * Create on 2022/12/21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SmsRequest {

    private Map<String, String> varParams;
    private Collection<String> phoneNos;
    private String template;

}
