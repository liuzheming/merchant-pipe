package com.merchant.pipeline.action;

import com.merchant.kernel.common.exception.ServiceException;
import com.merchant.kernel.common.utils.JsonUtils;
import com.merchant.kernel.common.utils.MsgBuilder;
import com.merchant.kernel.pipe.db.tables.pojos.ProcessAction;
import com.merchant.kernel.enums.ExecModeEnum;
import com.merchant.kernel.pipe.ActionStatusEnum;
import com.merchant.kernel.rpc.sdk.SeqGenerateRpc;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Minimal action factory for pipe runtime.
 */
@Slf4j
@Component
public class ActionFactory implements IActionFactory {

    @Resource
    private ActionRepo actionRepo;
    @Resource
    private SeqGenerateRpc seqGenerateRpc;
    @Resource
    private ApplicationContext applicationContext;

    @Override
    public IAction createAction(Long pipeId, String stageNo, ActionDef actionDef, ActionContext context)
        throws ClassNotFoundException {
        AbsAction action = instantiateAction(actionDef.getClazz());
        if (actionDef.getExecMode() == null) {
            actionDef.setExecMode(ExecModeEnum.MANUAL);
        }
        String itemNo = seqGenerateRpc.generate();
        action.init(itemNo, pipeId, stageNo, actionDef, context);
        return action;
    }

    @Override
    public IAction createAction4CustomTask(Long pipeId, String stageNo, ActionDef actionDef, ActionContext context)
        throws ClassNotFoundException {
        AbsAction action = instantiateAction(actionDef.getClazz());
        if (actionDef.getExecMode() == null) {
            actionDef.setExecMode(ExecModeEnum.MANUAL);
        }
        String itemNo = seqGenerateRpc.generate();
        action.manualInit(itemNo, pipeId, stageNo, actionDef, context);
        return action;
    }


    public List<IAction> loadActionByStageNo(String stageNo) {
        List<ProcessAction> actionEntities = actionRepo.listByStageNo(stageNo);
        List<String> actionNos = actionEntities.stream().map(ProcessAction::getItemNo).collect(
            Collectors.toList());
        return loadActions(actionNos);
    }

    public List<IAction> loadActionByPipeId(Long pipeId) {
        List<ProcessAction> actionEntities = actionRepo.listByPipeId(pipeId);
        List<String> actionNos = actionEntities.stream().map(ProcessAction::getItemNo).collect(Collectors.toList());
        return loadActions(actionNos);
    }

    public List<IAction> loadActions(List<String> actionNos) {
        List<IAction> actions = new ArrayList<>();
        for (String actionNo : actionNos) {
            actions.add(loadAction(actionNo));
        }
        return actions;
    }

    public IAction loadAction(String itemNo) {
        ProcessAction entity = actionRepo.get(itemNo);
        if (null == entity) {
            return null;
        }
        AbsAction action;
        try {
            action = instantiateAction(entity.getClazz());
        } catch (ClassNotFoundException e) {
            throw new ServiceException(MsgBuilder.build("class not found, itemNo:{}", itemNo));
        }

        action.setId(entity.getId());
        action.setItemNo(entity.getItemNo());
        action.setStageNo(entity.getStageNo());
        action.setPipeId(entity.getPipeId());
        action.setName(entity.getName());
        action.setClazz(entity.getClazz());
        action.setParam(JsonUtils.str2Obj(entity.getParam(), action.paramType()));
        action.setResult(null);
        action.setBizExplain(entity.getBizExplain());
        action.setExecCount(entity.getExecCount());
        action.setErrMsg(entity.getErrMsg());
        action.setStatus(ActionStatusEnum.of(entity.getStatus()));
        ExecModeEnum execMode = ExecModeEnum.of(entity.getExecMode());
        action.setExecMode(execMode == null ? ExecModeEnum.MANUAL : execMode);
        ActionContext context = JsonUtils.str2Obj(entity.getContext(), ActionContext.class);
        if (context == null) {
            context = new ActionContext();
        }
        action.setContext(context);
        return action;
    }

    private AbsAction instantiateAction(String clazz) throws ClassNotFoundException {
        if (StringUtils.isBlank(clazz)) {
            throw new ClassNotFoundException("action class is blank");
        }
        try {
            AbsAction action = (AbsAction) Class.forName(clazz).newInstance();
            applicationContext.getAutowireCapableBeanFactory().autowireBean(action);
            return action;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new ClassNotFoundException("failed to instantiate action: " + clazz, e);
        }
    }
}
