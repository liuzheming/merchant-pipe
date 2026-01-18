INSERT INTO process_action_pipe_def
    (name, code, proc_code, task_code, stage_def, deleted)
VALUES
    (
        'Sim Pipe',
        'sim_pipe',
        'sim_proc',
        'sim_task',
        '[{"actionDefs":[{"name":"Sample Exec OK","clazz":"com.merchant.pipe.simulator.SampleExecOkAction","execMode":"AUTO","extData":{}},{"name":"Sample Build OK","clazz":"com.merchant.pipe.simulator.SampleBuildOkAction","execMode":"AUTO","extData":{}}]}]',
        0
    );
