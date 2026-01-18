package com.ke.merchant.kernel.pipe.action;

/**
 * Create on 2022/12/5
 */
public class ActionContents {

    /**
     * 加盟商退出结构
     */
    public static final String FCH_DONE_PARAM = "fchDoneParam";

    /**
     * 是否需要执行门店合作主体变更pipe，true（默认）：需要，false-不需要
     */
    public static final String NEED_EXECUTE_PIPE = "needExecutePipe";
    /**
     * 需要执行当前pipe的门店id列表
     */
    public static final String NEED_EXECUTE_STORE_IDS = "needExecuteStoreIds";

    public static final String MSG_KEY = "msgKey";
    public static final String STORE_IDS = "storeIds";
    public static final String COOP_NO = "coopNo";
    public static final String CURRENT_COOP_NO = "currentCoopNo";
    public static final String NEW_COOP_NO = "newCoopNo";
    public static final String TERMINATE = "terminate";
    public static final String MSG_SCENE = "msg_scene";
    public static final String RESOURCE_LIST = "resource_list";
    public static final String ORDERS_KEY = "ordersKey";

    // BIZ_CONDITION_INFO
    public static final String BIZ_CONDITION_INFO = "bizConditionInfo";
    // CONFIG_ID
    public static final String CONFIG_ID = "configId";

    // expandStoreVersion
    public static final String EXPAND_STORE_VERSION = "expandStoreVersion";
    // FUND_INSTALLMENT_V2
    public static final String FUND_INSTALLMENT_V2 = "FUND_INSTALLMENT_V2";
    public static final String DIRECT_BRAND_MASTER_COOP_NO = "directBrandMasterCoopNo";


    public static final String DIRECT_COOP_NO_RELATE_STORE = "directCoopNoRelateStore";

    public static final String FRANCHISER_BRAND_MASTER_COOP_NO = "franchiserBrandMasterCoopNo";

    public static String CITY_CODE = "cityCode";
    public static final String BRAND_NAME = "brandName";
    // BRAND_COM_TAX_NO
    public static final String BRAND_COM_TAX_NO = "brandComTaxNo";
    public static final String NEW_BRAND_COM_TAX_NO = "newBrandComTaxNo";
    public static final String OLD_BRAND_COM_TAX_NO = "oldBrandComTaxNo";

    public static final String BRAND_NO = "brandNo";
    public static final String GID_LIST = "gidList";
    public static final String FCH_COM_MDM_CODE = "fchComMdmCode";
    // FCH_COM_TAX_NO
    public static final String FCH_COM_TAX_NO = "fchComTaxNo";
    public static final String NEW_FCH_COM_TAX_NO = "newFchComTaxNo";

    public static final String OLD_FCH_COM_TAX_NO = "oldFchComTaxNo";


    public static final String BRAND_NEW_COOP_NO = "brandNewCoopNo";

    // BRAND_COOP_NO
    public static final String BRAND_COOP_NO = "brandCoopNo";
    /**
     * 分账主体、电子章收费相关
     */
    public static String FINANCE_COM_TAX_NO = "financeComTaxNo";
    //fchComName
    public static String FCH_COM_NAME = "fchComName";

    public static String PROC_CODE = "procCode";
    /**
     * 品牌续签
     */
    public static final String BRAND_RENEW_MAIN = "brandRenewMain";
    public static final String BRAND_RENEW_CONFIG_TABLE = "brandRenewConfigTable";
    public static String SEAL_CHARGE_CONTRACT_NO = "sealChargeContractNo";
    public static String SEAL_CHARGE_ACCOUNT = "sealChargeAccount";
    public static String SEAL_CHARGE_BIZ_ORDER_NO = "sealChargeBizOrderNo";

    public static String INTENT_BIZ_ORDER_NO = "intentBizOrderNo";

    public static String INTENT_REFUND_BIZ_ORDER_NO = "intentRefundBizOrderNo";

    public static String INTENT_REFUND_CONTRACT_NO = "intentRefundContractNo";

    public static String INTENT_REFUND_AMOUNT = "intentRefundAmount";

    /**
     * 切换分账主体相关入参
     */
    public static final String OP_COM_MDM_CODE = "opComMdmCode";
    public static final String NEED_FRANCHISER_ID_FOR_OP_SWITCH = "needFranchiserIdForOpSwitch";
    public static final String FRANCHISER_ID = "franchiserId";
    public static final String RESOURCE_ORDER_KEY = "resourceOrderKey";
    public static final String STORE_ORG_CODES = "storeOrgCodes";

    /**
     * 设置门店合作主体相关入参
     */
    public static final String COM_TAX_NO = "comTaxNo";
    public static final String TARGET_STORE_ID = "storeId";
    public static final String TARGET_LINK_STORE_NO = "linkStoreNo";


    /**
     * 门店改签
     */
    public static final String FRANCHISER_START_DATE = "franchiserStartDate";

    public static final String FRANCHISER_END_DATE = "franchiserEndDate";

    public static final String FRANCHISER_CHANNEL_ATTACHMENT = "franchiser_channel_attachment";
    public static final String STORE_CHANNEL_ATTACHMENT = "store_channel_attachment";
    public static final String FEE_TABLE = "feeTable";
    public static String BRAND_COM_MDM_CODE = "brandComMdmCode";

    public static final String SAME_LEVEL_2_CODE = "sameLevel2Code";

    public static String EFFECTIVE_TIME = "effective_time";

    public static String SSC_PASS_TIME = "sscPassTime";
    /**
     * 资金相关
     */
    public static final String CUT_OFF_DATE = "cutOffDate";

    public static final String FEE_RATE_EFFECTIVE_TIME = "feeRateEffectiveTime";


    /**
     * 合同相关
     */
    // mainContract
    public static final String MAIN_CONTRACT = "mainContract";
    // jmBrandAddStore
    public static final String JM_BRAND_ADD_STORE = "jmBrandAddStore";
    // fchFeeSettlementInChange
    public static final String FCH_FEE_SETTLEMENT_IN_CHANGE = "fchFeeSettlementInChange";
    //fchExitStoreAgreement
    public static final String FCH_EXIT_STORE_AGREEMENT = "fchExitStoreAgreement";
    // BRAND_EXIT_STORE_AGREEMENT
    public static final String BRAND_EXIT_STORE_AGREEMENT = "brandExitStoreAgreement";
    //JM_BUSINESS_FRANCHISE_SUPPLEMENTAL
    public static final String JM_BUSINESS_FRANCHISE_SUPPLEMENTAL = "jmBusinessFranchiseSupplemental";
    // JM_FCH_COM_ATTACHMENT
    public static final String JM_FCH_COM_ATTACHMENT = "jmFchComAttachment";
    // BRAND_FEE_SETTLEMENT_IN_CHANGE
    public static final String BRAND_FEE_SETTLEMENT_IN_CHANGE = "brandFeeSettlementInChange";
    //JMS_RENEW_MAIN_CONTRACT
    public static final String JMS_RENEW_MAIN_CONTRACT = "jmsRenewMainContract";
    // JMS_RENEW_SUPPLEMENTAL_CONTRACT
    public static final String JMS_RENEW_SUPPLEMENTAL_CONTRACT = "jmsRenewSupplementalContract";
}
