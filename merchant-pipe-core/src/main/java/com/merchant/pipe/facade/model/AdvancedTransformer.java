package com.merchant.pipe.facade.model;

import com.google.common.collect.Lists;
import com.merchant.kernel.common.utils.JsonUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AdvancedTransformer {

    // 原始数据结构
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OriginGroup {
        private List<ActionDef> actionDefs;
        // getters & setters
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ActionDef {
        private String name;
        private String clazz;


        // getters & setters
    }

    // 目标数据结构
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TargetGroup {
        private List<EnrichedActionDef> actionDefs;
        // getters & setters
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class EnrichedActionDef {
        private String name;
        private String clazz;
        private String execMode = "AUTO";
        private Map<String, Object> extData = new HashMap<>();
        private String tip; // 新增字段
        // getters & setters
    }


    public static List<OriginGroup> reverseTransform(List<TargetGroup> targetList) {
        return targetList.stream().map(target -> {
            OriginGroup origin = new OriginGroup();
            origin.setActionDefs(target.getActionDefs().stream().map(e -> {
                ActionDef def = new ActionDef();
                def.setName(e.getName());
                def.setClazz(e.getClazz());
                return def;
            }).collect(Collectors.toList()));
            return origin;
        }).collect(Collectors.toList());
    }

    public static List<TargetGroup> transform(List<OriginGroup> originList) {
        // 创建分组规则（示例：根据任务名称前缀分组）
        Map<String, List<ActionDef>> grouped = originList.stream()
            .flatMap(g -> g.getActionDefs().stream())
            .collect(Collectors.groupingBy(
                action -> {
                    // 提取任务前缀，如"任务1-1" -> "任务1"
                    String[] parts = action.getName().split("-");
                    return parts.length > 0 ? parts[0] : "";
                }
            ));

        // 转换分组后的数据
        return grouped.values().stream().map(actionDefs -> {
            TargetGroup target = new TargetGroup();

            List<EnrichedActionDef> enriched = actionDefs.stream().map(def -> {
                EnrichedActionDef e = new EnrichedActionDef();
                e.setName(def.getName());
                e.setClazz(def.getClazz());

                return e;
            }).collect(Collectors.toList());

            target.setActionDefs(enriched);
            return target;
        }).collect(Collectors.toList());
    }

    // 帮我针对上面的方法制造一些case
    public static void main(String[] args) {
        OriginGroup group1 = new OriginGroup();
        List<ActionDef> actionDefs1 = new ArrayList<>();
        actionDefs1.add(new ActionDef("任务1-1", "com.merchant.controller.web.kesign.Action1"));
        actionDefs1.add(new ActionDef("任务1-2", "com.merchant.controller.web.kesign.Action2"));
        group1.setActionDefs(actionDefs1);

        OriginGroup group2 = new OriginGroup();
        List<ActionDef> actionDefs2 = new ArrayList<>();
        actionDefs2.add(new ActionDef("任务2-1", "com.merchant.controller.web.kesign.Action3"));
        actionDefs2.add(new ActionDef("任务2-2", "com.merchant.controller.web.kesign.Action4"));
        group2.setActionDefs(actionDefs2);

        List<TargetGroup> result = transform(Lists.asList(group1, new OriginGroup[]{group2}));
        System.out.println(result);


        String pipeStr = "[\n" +
            "    {\n" +
            "        \"actionDefs\": [\n" +
            "            {\n" +
            "                \"name\": \"选择目标6级组织\",\n" +
            "                \"clazz\": \"com.merchant.biz.acn.impl.flow.franchise.changebrand.action.PreStoreOrgTransferAction\",\n" +
            "                \"execMode\": \"AUTO\",\n" +
            "                \"tip\": \"城市人事选择翻牌后的目标2-6级组织\",\n" +
            "                \"extData\": {}\n" +
            "            },\n" +
            "            {\n" +
            "                \"name\": \"新房合同同步\",\n" +
            "                \"clazz\": \"com.merchant.biz.acn.impl.flow.franchise.storeChangeSign.action.NewHouseContractSyncForFchChangeAction\",\n" +
            "                \"execMode\": \"AUTO\",\n" +
            "                \"tip\": \"渠道合作框架协议同步给新房合同系统\",\n" +
            "                \"extData\": {}\n" +
            "            },\n" +
            "            {\n" +
            "                \"name\": \"组织门店平移\",\n" +
            "                \"clazz\": \"com.merchant.biz.acn.impl.flow.franchise.changebrand.action.MoveStoreOrgWithResOrderAction\",\n" +
            "                \"execMode\": \"AUTO\",\n" +
            "                \"tip\": \"将组织门店从原人事2-6级组织平移到新2-6级组织下（只用storeId）\",\n" +
            "                \"extData\": {}\n" +
            "            },\n" +
            "            {\n" +
            "                \"name\": \"等待理房通/电子章审核通过\",\n" +
            "                \"clazz\": \"com.merchant.biz.acn.impl.flow.franchise.changebrand.action.WaitStoreEhomePayAndEsealCheckPassesAction\",\n" +
            "                \"execMode\": \"AUTO\",\n" +
            "                \"tip\": \"等待门店申请的理房通/电子章均审核通过(不用动，需要的数据包里面的内容)\",\n" +
            "                \"extData\": {}\n" +
            "            },\n" +
            "            {\n" +
            "                \"name\": \"切换分账主体\",\n" +
            "                \"clazz\": \"com.merchant.biz.acn.impl.flow.franchise.changebrand.action.SwitchOpForStoreAction\",\n" +
            "                \"execMode\": \"AUTO\",\n" +
            "                \"adminAccounts\": \"zhangting150\",\n" +
            "                \"tip\": \"更新为最新设置的分账主体\",\n" +
            "                \"extData\": {}\n" +
            "            },\n" +
            "            {\n" +
            "                \"name\": \"设置门店合作主体\",\n" +
            "                \"clazz\": \"com.merchant.biz.acn.impl.flow.franchise.changebrand.action.SetStoreOwnerSubjectAction\",\n" +
            "                \"execMode\": \"AUTO\",\n" +
            "                \"adminAccounts\": \"zhangting150\",\n" +
            "                \"tip\": \"更新为最新设置的商户合作主体\",\n" +
            "                \"extData\": {}\n" +
            "            },\n" +
            "            {\n" +
            "                \"name\": \"保底费同步\",\n" +
            "                \"clazz\": \"com.merchant.biz.sync.action.MinimumFeeSyncAction\",\n" +
            "                \"execMode\": \"AUTO\",\n" +
            "                \"tip\": \"同步换改并后每个门店的保底费明细\",\n" +
            "                \"extData\": {}\n" +
            "            },\n" +
            "            {\n" +
            "                \"name\": \"创建电子章收费订单\",\n" +
            "                \"clazz\": \"com.merchant.biz.acn.impl.flow.franchise.storeChangeSign.action.FinanceEsealChargeAction\",\n" +
            "                \"execMode\": \"AUTO\",\n" +
            "                \"adminAccounts\": \"liuzheming004\",\n" +
            "                \"tip\": \"跟资金交互生成对应的电子章费应收订单\",\n" +
            "                \"extData\": {}\n" +
            "            }\n" +
            "        ]\n" +
            "    },\n" +
            "    {\n" +
            "        \"actionDefs\": [\n" +
            "            {\n" +
            "                \"name\": \"标准费率同步\",\n" +
            "                \"clazz\": \"com.merchant.biz.sync.action.StoreStandardSyncFeeRateV2Action\",\n" +
            "                \"execMode\": \"AUTO\",\n" +
            "                \"tip\": \"同步换改并后每个门店的费率明细\",\n" +
            "                \"extData\": {}\n" +
            "            }\n" +
            "        ]\n" +
            "    },\n" +
            "        {\n" +
            "            \"actionDefs\": [\n" +
            "                {\n" +
            "                    \"name\": \"处理历史优惠内容\",\n" +
            "                    \"clazz\": \"com.merchant.biz.sync.action.HistoricalRateDiscountsAction\",\n" +
            "                    \"execMode\": \"AUTO\",\n" +
            "                    \"tip\": \"处理历史优惠内容\",\n" +
            "                    \"extData\": {}\n" +
            "                }\n" +
            "            ]\n" +
            "        },\n" +
            "    {\n" +
            "        \"actionDefs\": [\n" +
            "            {\n" +
            "                \"name\": \"优惠券同步\",\n" +
            "                \"clazz\": \"com.merchant.biz.sync.action.FeeRateDiscountV2Action\",\n" +
            "                \"execMode\": \"AUTO\",\n" +
            "                \"tip\": \"优惠券同步\",\n" +
            "                \"extData\": {}\n" +
            "            }\n" +
            "        ]\n" +
            "    },\n" +
            "    {\n" +
            "        \"actionDefs\": [\n" +
            "            {\n" +
            "                \"name\": \"费率截断\",\n" +
            "                \"clazz\": \"com.merchant.biz.sync.action.CutOffPercentageFeeRateActionV2\",\n" +
            "                \"execMode\": \"AUTO\",\n" +
            "                \"tip\": \"费率截断\",\n" +
            "                \"extData\": {}\n" +
            "            }\n" +
            "        ]\n" +
            "    }\n" +
            "]\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "[\n" +
            "    {\n" +
            "        \"actionDefs\": [\n" +
            "            {\n" +
            "                \"name\": \"选择目标6级组织\",\n" +
            "                \"clazz\": \"com.merchant.biz.acn.impl.flow.franchise.changebrand.action.PreStoreOrgTransferAction\",\n" +
            "                \"execMode\": \"AUTO\",\n" +
            "                \"tip\": \"城市人事选择翻牌后的目标2-6级组织\",\n" +
            "                \"extData\": {}\n" +
            "            },\n" +
            "            {\n" +
            "                \"name\": \"新房合同同步\",\n" +
            "                \"clazz\": \"com.merchant.biz.acn.impl.flow.franchise.storeChangeSign.action.NewHouseContractSyncForFchChangeAction\",\n" +
            "                \"execMode\": \"AUTO\",\n" +
            "                \"tip\": \"渠道合作框架协议同步给新房合同系统\",\n" +
            "                \"extData\": {}\n" +
            "            },\n" +
            "            {\n" +
            "                \"name\": \"组织门店平移\",\n" +
            "                \"clazz\": \"com.merchant.biz.acn.impl.flow.franchise.changebrand.action.MoveStoreOrgWithResOrderAction\",\n" +
            "                \"execMode\": \"AUTO\",\n" +
            "                \"tip\": \"将组织门店从原人事2-6级组织平移到新2-6级组织下（只用storeId）\",\n" +
            "                \"extData\": {}\n" +
            "            },\n" +
            "            {\n" +
            "                \"name\": \"等待理房通/电子章审核通过\",\n" +
            "                \"clazz\": \"com.merchant.biz.acn.impl.flow.franchise.changebrand.action.WaitStoreEhomePayAndEsealCheckPassesAction\",\n" +
            "                \"execMode\": \"AUTO\",\n" +
            "                \"tip\": \"等待门店申请的理房通/电子章均审核通过(不用动，需要的数据包里面的内容)\",\n" +
            "                \"extData\": {}\n" +
            "            },\n" +
            "            {\n" +
            "                \"name\": \"切换分账主体\",\n" +
            "                \"clazz\": \"com.merchant.biz.acn.impl.flow.franchise.changebrand.action.SwitchOpForStoreAction\",\n" +
            "                \"execMode\": \"AUTO\",\n" +
            "                \"adminAccounts\": \"zhangting150\",\n" +
            "                \"tip\": \"更新为最新设置的分账主体\",\n" +
            "                \"extData\": {}\n" +
            "            },\n" +
            "            {\n" +
            "                \"name\": \"设置门店合作主体\",\n" +
            "                \"clazz\": \"com.merchant.biz.acn.impl.flow.franchise.changebrand.action.SetStoreOwnerSubjectAction\",\n" +
            "                \"execMode\": \"AUTO\",\n" +
            "                \"adminAccounts\": \"zhangting150\",\n" +
            "                \"tip\": \"更新为最新设置的商户合作主体\",\n" +
            "                \"extData\": {}\n" +
            "            },\n" +
            "            {\n" +
            "                \"name\": \"创建电子章收费订单\",\n" +
            "                \"clazz\": \"com.merchant.biz.acn.impl.flow.franchise.storeChangeSign.action.FinanceEsealChargeAction\",\n" +
            "                \"execMode\": \"AUTO\",\n" +
            "                \"adminAccounts\": \"liuzheming004\",\n" +
            "                \"tip\": \"跟资金交互生成对应的电子章费应收订单\",\n" +
            "                \"extData\": {}\n" +
            "            }\n" +
            "        ]\n" +
            "    },\n" +
            "    {\n" +
            "        \"actionDefs\": [\n" +
            "            {\n" +
            "                \"name\": \"标准费率同步\",\n" +
            "                \"clazz\": \"com.merchant.biz.sync.action.StoreStandardSyncFeeRateV2Action\",\n" +
            "                \"execMode\": \"AUTO\",\n" +
            "                \"tip\": \"同步换改并后每个门店的费率明细\",\n" +
            "                \"extData\": {}\n" +
            "            }\n" +
            "        ]\n" +
            "    },\n" +
            "    {\n" +
            "        \"actionDefs\": [\n" +
            "            {\n" +
            "                \"name\": \"优惠券同步\",\n" +
            "                \"clazz\": \"com.merchant.biz.sync.action.FeeRateDiscountV2Action\",\n" +
            "                \"execMode\": \"AUTO\",\n" +
            "                \"tip\": \"优惠券同步\",\n" +
            "                \"extData\": {}\n" +
            "            }\n" +
            "        ]\n" +
            "    },\n" +
            "    {\n" +
            "        \"actionDefs\": [\n" +
            "            {\n" +
            "                \"name\": \"费率截断\",\n" +
            "                \"clazz\": \"com.merchant.biz.sync.action.CutOffPercentageFeeRateActionV2\",\n" +
            "                \"execMode\": \"AUTO\",\n" +
            "                \"tip\": \"费率截断\",\n" +
            "                \"extData\": {}\n" +
            "            }\n" +
            "        ]\n" +
            "    }\n" +
            "]";

        // 将json转化为TargetAction对象
        List<TargetGroup> targetActions = JsonUtils.str2List(pipeStr, TargetGroup.class);
        List<OriginGroup> originGroups = reverseTransform(targetActions);
        System.out.println(JsonUtils.obj2Str(originGroups));


    }


}
