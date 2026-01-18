package com.ke.merchant.kernel.pipe.action;

import com.ke.merchant.kernel.enums.ExecModeEnum;

/**
 * Description:
 * <p>
 *
 * @author lzm
 * @date 2023/9/18
 */
public interface IActionService {

    void resetAction(String itemNo);

    void resetAction(String itemNo, ExecModeEnum mode);


}
