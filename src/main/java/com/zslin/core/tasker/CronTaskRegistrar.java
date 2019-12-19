package com.zslin.core.tasker;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.config.CronTask;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CronTaskRegistrar implements DisposableBean {

    ///之前这里初始化16->private final Map<Runnable, ScheduledTask> scheduledTasks = new ConcurrentHashMap<>(16);
//    private final Map<Runnable, ScheduledTask> scheduledTasks = new ConcurrentHashMap<>();
    private final Map<String, ScheduledTask> scheduledTasks = new HashMap<>();

    @Autowired
    private TaskScheduler taskScheduler;

    public TaskScheduler getScheduler() {
        return this.taskScheduler;
    }

    public void show() {
        System.out.println("----------size: "+ this.scheduledTasks.size());
        Set<String> set = this.scheduledTasks.keySet();
        for(String s : set) {
            System.out.println("--------->" + s);
            System.out.println(this.scheduledTasks.remove(s));
        }
    }

    public Map<String, ScheduledTask> list() {
        return this.scheduledTasks;
    }

    /**
     * 新增定时任务
     * @param task
     * @param cronExpression
     */
    public void addCronTask(SchedulingRunnable task, String cronExpression) {
        addCronTask(task.getTaskName(), new CronTask(task, cronExpression));
    }

    public void addCronTask(String taskName, CronTask cronTask) {
        removeByName(taskName); //先删除
        if (cronTask != null) {
            SchedulingRunnable task = (SchedulingRunnable) cronTask.getRunnable();
            if (this.scheduledTasks.containsKey(task)) {
                removeCronTask(task);
            }

            this.scheduledTasks.put(taskName, scheduleCronTask(cronTask));
            //System.out.println("----------current size:: "+this.scheduledTasks.size());
        }
    }

    public void removeByName(String taskName) {
        ScheduledTask scheduledTask = this.scheduledTasks.remove(taskName);
        //System.out.println("删除--------->"+scheduledTask);
        if (scheduledTask != null) {
            scheduledTask.cancel();
        }
    }

    /**
     * 移除定时任务
     * @param task
     */
    public void removeCronTask(SchedulingRunnable task) {
        removeByName(task.getTaskName());
        /*ScheduledTask scheduledTask = this.scheduledTasks.remove(task.getTaskName());
        if (scheduledTask != null)
            scheduledTask.cancel();*/
    }

    public ScheduledTask scheduleCronTask(CronTask cronTask) {
        ScheduledTask scheduledTask = new ScheduledTask();
        scheduledTask.future = this.taskScheduler.schedule(cronTask.getRunnable(), cronTask.getTrigger());

        return scheduledTask;
    }

    @Override
    public void destroy() {
        for (ScheduledTask task : this.scheduledTasks.values()) {
            task.cancel();
        }
        this.scheduledTasks.clear();
    }
}
