package com.zslin.core.tasker;

import com.zslin.core.model.BaseTask;
import lombok.Data;

@Data
public class TaskDto {

//    private SchedulingRunnable runner;

    private BaseTask runner;
    private ScheduledTask task;

    public TaskDto(BaseTask runner, ScheduledTask task) {
        this.runner = runner;
        this.task = task;
    }
}
