package com.zslin.test;

import com.zslin.core.tasker.CronTaskRegistrar;
import com.zslin.core.tasker.SchedulingRunnable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles(value = "zsl")
public class TaskTest {

    @Autowired
    CronTaskRegistrar cronTaskRegistrar;

    @Test
    public void testTask() throws InterruptedException {
        SchedulingRunnable task = new SchedulingRunnable("noParamTask","demoTask", "taskNoParams");
//        task.
        cronTaskRegistrar.addCronTask(task, "0/10 * * * * ?");

        // 便于观察
        Thread.sleep(3000000);
    }

    @Test
    public void testHaveParamsTask() throws InterruptedException {
        SchedulingRunnable task = new SchedulingRunnable("hasParamTask","demoTask", "taskWithParams", "haha", 23);
        cronTaskRegistrar.addCronTask(task, "0/10 * * * * ?");

        // 便于观察
        Thread.sleep(3000000);
    }

    @Test
    public void remove1() {
        cronTaskRegistrar.removeByName("noParamTask");
    }

    @Test
    public void remove2() {
        cronTaskRegistrar.removeByName("hasParamTask");
    }

    @Test
    public void show() {
        cronTaskRegistrar.show();
    }
}
