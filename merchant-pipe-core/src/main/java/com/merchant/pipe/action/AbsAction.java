package com.merchant.pipe.action;

import static com.merchant.pipe.ActionStatusEnum.EXEC_FAIL;
import static com.merchant.pipe.ActionStatusEnum.EXEC_SKIP;
import static com.merchant.pipe.ActionStatusEnum.EXEC_SUCC;
import static com.merchant.pipe.ActionStatusEnum.INIT_SUCC;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import com.merchant.common.constant.UserInfo;
import com.merchant.common.exception.ServiceException;
import com.merchant.common.utils.UserManager;
import com.merchant.enums.ExecModeEnum;
import com.merchant.pipe.ActionSkipException;
import com.merchant.pipe.ActionStatusEnum;
import com.merchant.pipe.ActionStuckException;
import com.merchant.pipe.pipe.BuildResult;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 抽象 Action
 * <p>
 * Create on 2022/2/17
 */
@Slf4j
@Data
public abstract class AbsAction<P, R> implements IAction {

    private Long id;
    private String itemNo;
    private String stageNo;
    private Long pipeId;
    private String name;
    private String clazz;
    protected P param;
    protected R result;
    private String bizExplain;
    private Integer execCount;
    protected String errMsg = StringUtils.EMPTY;
    private ActionStatusEnum status;
    private ExecModeEnum execMode;
    private ActionContext context;
    private String inputScript;
    protected Long processId;
    protected Long taskId;
    protected String taskCode;
    protected String ucid;
    protected Long startUserId;
    private String tip;
    protected String adminAccounts;
    protected LocalDateTime doneTime;
    protected LocalDateTime ctime;

    @Resource
    private ActionRepo actionRepo;
    @Resource
    private TransactionTemplate transactionTemplate;

    public static final String LOCK_NAME = "actions.run";

    protected AbsAction() {

    }

    public void init(String itemNo, Long pipeId, String stageNo, ActionDef actionDef, ActionContext context) {
        if (null == context) {
            context = new ActionContext();
        }
        this.itemNo = itemNo;
        this.pipeId = pipeId;
        this.stageNo = stageNo;
        this.taskId = context.getTaskId();
        this.taskCode = context.getTaskCode();
        this.processId = context.getProcessId();
        this.name = actionDef.getName();
        if (StringUtils.isNotEmpty(actionDef.getTip())) {
            this.tip = actionDef.getTip();
        }
        this.execMode = actionDef.getExecMode();
        this.clazz = actionDef.getClazz();
        this.ucid = context.getUcid();
        this.context = context;
        this.inputScript = actionDef.getInputScript();
        this.adminAccounts = actionDef.getAdminAccounts();
        UserInfo userInfo = UserManager.getUser() == null ? UserManager.getDefaultUser() : UserManager.getUser();
        if (null != userInfo) {
            this.startUserId = userInfo.getUcId();
        }
        status = INIT_SUCC;
        if (this.checkSkip()) {
            status = EXEC_SKIP;
        }
        prepareRetry(actionDef);
        LOGGER.info("action init completed, wait to execute... Action:{}", this);
        save();
    }


    public void manualInit(String itemNo, Long pipeId, String stageNo, ActionDef actionDef, ActionContext context) {
        if (null == context) {
            context = new ActionContext();
        }
        this.itemNo = itemNo;
        this.pipeId = pipeId;
        this.stageNo = stageNo;
        this.taskId = context.getTaskId();
        this.taskCode = context.getTaskCode();
        this.processId = context.getProcessId();
        this.name = actionDef.getName();
        if (StringUtils.isNotEmpty(actionDef.getTip())) {
            this.tip = actionDef.getTip();
        }
        this.execMode = actionDef.getExecMode();
        this.clazz = actionDef.getClazz();
        this.ucid = context.getUcid();
        this.context = context;
        this.inputScript = actionDef.getInputScript();
        this.adminAccounts = actionDef.getAdminAccounts();
        status = ActionStatusEnum.WAIT_EXEC;
        if (this.checkSkip()) {
            status = EXEC_SKIP;
        }
        prepareRetry(actionDef);
        LOGGER.info("action init completed, wait to execute... Action:{}", this);
        save(actionDef);
    }

    /**
     * 构建表单额外需要的initData数据，写入到表单InitData中
     *
     * @return
     */
    protected Map<String, String> buildActionAdditionalData() {
        return null;
    }

    /**
     * action初始化的时候检测是否可以跳过
     *
     * @return
     */
    public boolean checkSkip() {
        return false;
    }


    /**
     * 手动完成支持自动完成
     * 根据场景进行判断
     *
     * @return
     */
    protected boolean manualCompleteSupportAutoChoose() {
        return false;
    }


    protected BuildResult build() throws ActionSkipException {
        try {
            param = buildParam();
        } catch (ActionSkipException e) {
            errMsg = e.getMessage();
            return new BuildResult(true);
        } catch (Exception e) {
            status = EXEC_FAIL;
            errMsg = e.getMessage();
            throw e;
        } finally {
            update();
        }
        return new BuildResult(false);
    }

    protected P buildParam() throws ActionSkipException {
        return null;
    }

    /**
     * @throws ActionStuckException 用于标识方法未执行失败, 只是未完成
     */
    protected abstract ActionResult<R> doExecute(P param) throws ActionStuckException;


    /**
     * @throws ActionStuckException 用于自动任务完成
     */
    public ActionResult<R> tryExecute(P param) throws ActionStuckException {
        throw new ActionStuckException("Action not support tryExecute method");
    }


    /**
     * 用于数据包之间的交互，根据拼凑的param数据
     *
     * @param
     * @return
     * @throws ActionStuckException
     */

    public void beforeComplete() {
    }

    /**
     * 需要实现buttonList的action需要实现这个接口
     *
     * @return
     */
    public List<ActionButton> getButtonList() {
        return Lists.newArrayList();
    }


    /**
     * 展示表单是否是完成表单
     *
     * @return
     */
    public boolean showFormEqualCompleteForm() {
        return true;
    }


    /**
     * 任意状态 => EXEC_SKIP
     * <p>
     * 跳过此 action 不执行，跳过后，action 状态变为 EXEC_SKIP，EXEC_SKIP 状态也代表执行成功
     */
    public ActionResult<R> skip() {
        status = EXEC_SKIP;
        this.doneTime = LocalDateTime.now();
        update();
        LOGGER.info("Skip action, itemNo:{}", itemNo);
        return new ActionResult<>(true);
    }

    protected abstract TypeReference<P> paramType();

    /**
     * 执行 action 操作:  INIT_SUCC => EXEC_SUCC  or EXEC_FAIL
     * <p>
     * 如果 action 当前的状态不是 INIT_SUCC 时，报错
     *
     * @return true:成功，false:卡位中
     */
    public final ActionResult<R> execute() {
        ActionResult<R> actionResult;
        try {
            LOGGER.info("will execute action , itemNo:{}, taskId:{}, processId:{}", itemNo, taskId, processId);
            actionResult = executeWithRetry();
            if (!actionResult.getSuccess()) {
                LOGGER.info("action execute fail, wait next try, Action:{}", this);
                return actionResult;
            }
            result = actionResult.getData();
            bizExplain = actionResult.getBizExplain();
            status = EXEC_SUCC;
            doneTime = LocalDateTime.now();
            errMsg = StringUtils.EMPTY;
            update();
            LOGGER.info("action execute success, Action:{}", this);
        } catch (ActionStuckException e) {
            return new ActionResult<>(false);
        } catch (ActionSkipException e) {
            status = EXEC_SKIP;
            return new ActionResult<>(true);
        } catch (Exception e) {
            status = EXEC_FAIL;
            errMsg = e.getMessage();
            throw e;
        } finally {
            update();
        }
        return actionResult;
    }


    public final void commitAndComplete() {
        beforeComplete();
        status = EXEC_SUCC;
        doneTime = LocalDateTime.now();
        errMsg = StringUtils.EMPTY;
        update();
        LOGGER.info("action commit packet success, Action:{}", this);
    }


    /**
     * 执行 action，如果执行失败，进行重试
     */
    private ActionResult<R> executeWithRetry() {

        if (null == this.getContext()) {
            this.setContext(new ActionContext());
        }
        ActionContext.RetryParam retryParam = this.getContext().getRetryParam();
        ExponentialBackOffPolicy backOff = new ExponentialBackOffPolicy();
        backOff.setInitialInterval(retryParam.getInitialIntervalMs());
        backOff.setMaxInterval(retryParam.getMaxIntervalMs());
        backOff.setMultiplier(retryParam.getMultiplier());

        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setBackOffPolicy(backOff);
        ActionResult<R> resultData = retryTemplate.execute(ctx -> {
            LOGGER.info("execute action with retry, retryTimes:{}", ctx.getRetryCount());
            Integer execCount = this.getExecCount();
            this.setExecCount(execCount == null ? 1 : execCount + 1);
            return transactionTemplate.execute(status -> doExecute(param));
        });
        if (null == resultData) {
            LOGGER.warn("actionResult should not be null, itemNo:{}, class:{}", getItemNo(), getClazz());
            return new ActionResult<>(true);
        }
        if (null != resultData.getOutput()) {
            this.getContext().setOutput(resultData.getOutput());
        }
        return resultData;
    }

    public void save() {
        actionRepo.save(this);
    }


    public void save(ActionDef actionDef) {
        actionRepo.save(this);
    }

    public void update() {
        actionRepo.update(this);
    }


    public String getType() {
        return StringUtils.EMPTY;
    }

    public Boolean done() {
        return EXEC_SUCC.equals(this.status) || EXEC_SKIP.equals(this.status);
    }

    public void prepareRetry(ActionDef actionDef) {

        ActionContext.RetryParam retryParam = new ActionContext.RetryParam();
        if (null != actionDef.getMaxAttempts()) {
            retryParam.setMaxAttempts(actionDef.getMaxAttempts());
        }
        if (null != actionDef.getInitialInterval()) {
            retryParam.setInitialIntervalMs(actionDef.getInitialInterval());
        }
        if (null != actionDef.getMaxInterval()) {
            retryParam.setMaxIntervalMs(actionDef.getMaxInterval());
        }
        if (null != actionDef.getMultiplier()) {
            retryParam.setMultiplier(actionDef.getMultiplier());
        }

        context.setRetryParam(retryParam);
    }

    protected String findInput(String key) {
        return this.getContext().getInput().findPath(key).asText(StringUtils.EMPTY);
    }

    protected String findOutput(String key) {
        return this.getContext().getOutput().findPath(key).asText(StringUtils.EMPTY);
    }


    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("AbsAction{");
        sb.append("id=").append(id);
        sb.append(", itemNo='").append(itemNo).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", errMsg='").append(errMsg).append('\'');
        sb.append(", status=").append(status);
        sb.append(", execCount=").append(execCount);
        sb.append(", execMode=").append(execMode);
        sb.append(", processId=").append(processId);
        sb.append(", taskId=").append(taskId);
        sb.append(", taskCode='").append(taskCode).append('\'');
        sb.append(", ucid='").append(ucid).append('\'');
        sb.append(", ctime=").append(ctime);
        sb.append('}');
        return sb.toString();
    }

    /**
     * 是否需要执行该pipe，默认需要
     *
     * @return
     */
    public Boolean needExecutePipe() {
        return true;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ActionButton {
        /**
         * 是否需要跳转新页面，true-是
         */
        private Boolean jumpToNewPage;

        /**
         * 点击按钮，是否需要调接口，true-是
         */
        private Boolean needApi;

        /**
         * 按钮类型
         */
        private String instruction;


        /**
         * 功能key
         */
        private String functionKey;


        /**
         * 按钮名称
         */
        private String label;
        /**
         * 按钮链接
         */
        private String link;
        /**
         * 按钮h5链接
         */
        private String h5link;
        private Dialog dialog;
        private ExtraInfo extraInfo;

        public ActionButton(String label, String link) {
            this.label = label;
            this.link = link;
        }

        public ActionButton(String label, String link, String h5link) {
            this.label = label;
            this.link = link;
            this.h5link = h5link;
        }


        public ActionButton(String instruction, String label, String link, String h5link) {
            this.instruction = instruction;
            this.label = label;
            this.link = link;
            this.h5link = h5link;
        }
    }

    public static class Dialog {
        private String title;
        private String body;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }
    }

    public static class ExtraInfo {
        private String type;
        private String value;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class ButtonConstant {
        public static String ReadOnly = "ReadOnly";
        public static String Dialog = "Dialog";
    }

    protected void warnContractTagNoSearched() {
        LOGGER.warn("合同标签未命中, actionItemNo:{}", getItemNo());
    }

    protected void warnContractTagNoSearched(String tagName) {
        LOGGER.warn("合同标签未命中, actionItemNo:{}, tagName:{}", getItemNo(), tagName);
    }

    protected void throwMainContractNotFoundException() {
        throw new ServiceException("主合同未找到, actionItemNo:" + getItemNo());
    }

    protected void throwContractNotFoundException(String contractName) {
        throw new ServiceException("合同未找到, actionItemNo:" + getItemNo() + ", contractName:" + contractName);
    }
}
