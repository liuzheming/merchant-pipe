ALTER TABLE `process_action_pipe`
    ADD COLUMN `pipe_organizer` VARCHAR(256) NOT NULL DEFAULT ''  COMMENT '主办人',
    ADD COLUMN `pipe_type` int(20) NOT NULL DEFAULT '0'  COMMENT '并网类型';
