CREATE TABLE `process_action`
(
    `id`         bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'pk',
    `item_no`    varchar(64)   NOT NULL DEFAULT '' COMMENT '流水号',
    `group_id`   varchar(64)            DEFAULT '' COMMENT 'groupId\n',
    `stage_no`   varchar(64)   NOT NULL DEFAULT '' COMMENT 'action 所属的阶段编号',
    `name`       varchar(255)  NOT NULL DEFAULT '' COMMENT 'action名称，在actionDef中定义',
    `context`    text COMMENT 'context',
    `clazz`      varchar(255)  NOT NULL DEFAULT '' COMMENT 'action的实现类，全类名',
    `param`      text COMMENT '请求信息',
    `result`     text COMMENT '响应信息',
    `err_msg`    varchar(5120) NOT NULL DEFAULT '',
    `tip`        varchar(64)   NOT NULL DEFAULT '' COMMENT 'tractId',
    `task_id`    bigint(20) NOT NULL DEFAULT '0' COMMENT '任务id',
    `task_code`  varchar(64)   NOT NULL DEFAULT '' COMMENT '任务配置id',
    `process_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '流程实例id',
    `status`     varchar(64)   NOT NULL DEFAULT '' COMMENT 'INIT_FAIL、INIT_SUCC、EXEC_FAIL、EXEC_SUCC、EXEC_SKIP',
    `exec_mode`  varchar(64)   NOT NULL DEFAULT '' COMMENT '执行方式: AUTO, MANUAL, SKIP',
    `ucid`       varchar(32)   NOT NULL DEFAULT '' COMMENT 'ucid',
    `done_time`  timestamp     NOT NULL DEFAULT '1970-01-02 00:00:00' COMMENT 'action 完成时间',
    `ctime`      timestamp     NOT NULL DEFAULT '1970-01-02 00:00:00' COMMENT 'ctime',
    `mtime`      timestamp     NOT NULL DEFAULT '1970-01-02 00:00:00' ON UPDATE CURRENT_TIMESTAMP COMMENT 'utime',
    `deleted`    int(11) NOT NULL DEFAULT '0' COMMENT '已被删除',
    PRIMARY KEY (`id`) USING BTREE,
    KEY          `process_action_item_no_index` (`item_no`) USING BTREE,
    KEY          `process_action_process_id_index` (`process_id`) USING BTREE,
    KEY          `process_action_task_id_index` (`task_id`) USING BTREE,
    KEY          `process_action_group_id_index` (`group_id`),
    KEY          `process_action_stage_no_index` (`stage_no`)
) ENGINE=InnoDB AUTO_INCREMENT=21851 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='process_action';

CREATE TABLE `process_action_group`
(
    `id`           bigint(11) NOT NULL AUTO_INCREMENT COMMENT 'pk',
    `name`         varchar(64) NOT NULL DEFAULT '' COMMENT 'name',
    `task_id`      bigint(20) NOT NULL DEFAULT '0' COMMENT 'taskId',
    `process_id`   bigint(20) NOT NULL DEFAULT '0' COMMENT 'processId',
    `status`       varchar(64) NOT NULL DEFAULT '' COMMENT 'status',
    `trigger_time` timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '定时触发时间',
    `ctime`        timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'ctime',
    `mtime`        timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'mtime',
    PRIMARY KEY (`id`),
    KEY            `process_action_group_taskId_idx` (`task_id`),
    KEY            `process_action_group_processId_idx` (`process_id`)
) ENGINE=InnoDB AUTO_INCREMENT=981 DEFAULT CHARSET=utf8mb4 COMMENT='actionGroup';

CREATE TABLE `process_action_pipe`
(
    `id`            bigint(11) NOT NULL AUTO_INCREMENT COMMENT 'pk',
    `name`          varchar(64) NOT NULL DEFAULT '' COMMENT 'name',
    `task_id`       bigint(20) NOT NULL DEFAULT '0' COMMENT 'taskId',
    `process_id`    bigint(20) NOT NULL DEFAULT '0' COMMENT 'processId',
    `status`        varchar(64) NOT NULL DEFAULT '' COMMENT 'status',
    `head_stage_no` varchar(64)          default '' not null comment '头结点 stageNo',
    `tail_stage_no` varchar(64)          default '' not null comment '尾节点 stageNo',
    `trigger_time`  timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '定时触发时间',
    `ctime`         timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'ctime',
    `mtime`         timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'mtime',
    PRIMARY KEY (`id`),
    KEY             `process_action_group_taskId_idx` (`task_id`),
    KEY             `process_action_group_processId_idx` (`process_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3000 DEFAULT CHARSET=utf8mb4 COMMENT='actionPipe';

-- ----------------------------------------------------------------------------------------------
-- ----------------------------------------------------------------------------------------------

alter table process_action change group_id pipe_id bigint default 0 not null comment 'pipeId';

create table process_action_pipe_def
(
    id        bigint auto_increment primary key comment 'PK',
    name      varchar(64) default '' not null comment 'name',
    code      varchar(64) default '' not null comment 'code',
    proc_code varchar(64) default '' not null comment 'proc_code',
    task_code varchar(64) default '' not null comment 'task_code',
    stage_def text comment 'stage_def',
    ctime     timestamp              NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'ctime',
    mtime     timestamp              NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'mtime',
    deleted   int(11) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
    KEY       `pipe_def_code_idx` (`code`),
    KEY       `pipe_def_proc_code_task_code_idx` (`proc_code`,`task_code`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 7315
  DEFAULT CHARSET = utf8mb4
  ROW_FORMAT = DYNAMIC COMMENT ='Pipe 定义表';


create table process_action_stage
(
    id            bigint auto_increment primary key comment 'PK',
    pipe_id       bigint      default 0  not null comment 'pipe_id',
    stage_no      varchar(64) default '' not null comment 'stage_no',
    status        varchar(64) default '' not null comment 'status',
    next_stage_no varchar(64) default '' not null comment 'next_stage_no',
    ctime         timestamp              NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'ctime',
    mtime         timestamp              NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'mtime',
    deleted       int(11) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
    KEY       `pipe_stage_stage_no_idx` (`stage_no`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  ROW_FORMAT = DYNAMIC COMMENT ='stage 实例表';


alter table process_action_pipe
    add context text  comment 'pipe 上下文' after process_id;

alter table process_action add column admin_accounts  varchar(512)  default ''   not null  comment
    '管理员用户名称, 逗号分隔' after ucid;


--  ------

alter table process_action_pipe
    modify mtime timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment 'mtime';

alter table process_action_pipe
    add done_time datetime default '1970-01-02 00:00:00' not null comment '完成时间' after trigger_time;

alter table process_action_stage
    add done_time datetime default '1970-01-02 00:00:00' not null comment '完成时间' after next_stage_no;

alter table process_action
    add exec_count int default 0 not null comment '执行次数' after result;

alter table process_action
    add input_script text null comment 'input 参数映射脚本' after context;

CREATE TABLE process_action_op_log
(
    `id`             bigint       NOT NULL AUTO_INCREMENT COMMENT 'PK',
    `resource_no`   varchar(255) NOT NULL COMMENT 'resource_no',
    `operation_type` varchar(255) NOT NULL COMMENT '操作类型',
    `operation_time` datetime     NOT NULL default now() COMMENT '执行时间',
    `address`        varchar(255) NOT NULL DEFAULT 0 COMMENT '执行机器地址',
    `username`       varchar(255) NOT NULL COMMENT '用户名',
    `ucid`           bigint       NOT NULL DEFAULT 0 COMMENT '用户ucid',
    `ctime`          datetime     NOT NULL DEFAULT now() COMMENT 'ctime',
    PRIMARY KEY (`id`),
    INDEX `idx_resource_no` (`resource_no`) USING BTREE COMMENT 'resource_no index'
) comment 'Action 操作日志表';

alter table process_action
    add biz_explain text null comment '业务信息说明' after result;
