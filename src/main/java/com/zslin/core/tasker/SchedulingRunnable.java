package com.zslin.core.tasker;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Objects;

@Slf4j
@Data
public class SchedulingRunnable implements Runnable {

    private String taskName;
    private String beanName;

    private String methodName;

    private Object[] params;

    /*public SchedulingRunnable(String beanName, String methodName) {
        this(beanName, methodName, null);
    }*/

    public SchedulingRunnable(String taskName, String beanName, String methodName, Object...params ) {
        this.taskName = taskName;
        this.beanName = beanName;
        this.methodName = methodName;
        this.params = params;
    }

    @Override
    public void run() {
        log.info("定时任务开始执行 - name:{}, bean：{}，方法：{}，参数：{}", taskName, beanName, methodName, params);
        long startTime = System.currentTimeMillis();

        try {
            Object target = SpringContextUtils.getBean(beanName);

            Method method = null;
            if (null != params && params.length > 0) {
                Class<?>[] paramCls = new Class[params.length];
                for (int i = 0; i < params.length; i++) {
                    paramCls[i] = params[i].getClass();
                }
                method = target.getClass().getDeclaredMethod(methodName, paramCls);
            } else {
                method = target.getClass().getDeclaredMethod(methodName);
            }

            ReflectionUtils.makeAccessible(method);
            if (null != params && params.length > 0) {
                method.invoke(target, params);
            } else {
                method.invoke(target);
            }
        } catch (Exception ex) {
            log.error(String.format("定时任务执行异常 - bean：%s，方法：%s，参数：%s ", beanName, methodName, params), ex);
        }

        long times = System.currentTimeMillis() - startTime;
        log.info("定时任务执行结束 - name:{}, bean：{}，方法：{}，参数：{}，耗时：{} 毫秒", taskName, beanName, methodName, params, times);
        System.out.println("----------------------------------------------------------------");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SchedulingRunnable that = (SchedulingRunnable) o;
        if (params == null) {
            return beanName.equals(that.beanName) &&
                    methodName.equals(that.methodName) &&
                    that.params == null;
        }

        return beanName.equals(that.beanName) &&
                methodName.equals(that.methodName) &&
                params.equals(that.params);
    }

    @Override
    public int hashCode() {
        if (params == null) {
            return Objects.hash(beanName, methodName);
        }

        return Objects.hash(beanName, methodName, params);
    }
}
