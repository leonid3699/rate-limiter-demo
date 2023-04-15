package com.leonid.demo.aspect;

import com.leonid.demo.annotation.LogRuntime;
import com.leonid.demo.util.Util;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LogRuntimeAspect {
    final Logger logger = LoggerFactory.getLogger(LogRuntimeAspect.class);

    public static class Timer {
        private long startTime;

        public Timer(long startTime) {
            this.startTime = startTime;
        }

        public long getRunTime(long endTime) {
            return (endTime - startTime);
        }

        public long getStartTime() {
            return startTime;
        }

    }

    @Around("@annotation(com.leonid.demo.annotation.LogRuntime)")
    public Object logRuntime(ProceedingJoinPoint joinPoint) throws Throwable {
        Timer timer = new Timer(System.currentTimeMillis());
        Object instance = joinPoint.proceed();
        String methodKey = Util.getMethodKey(joinPoint);
        logger.info("{} runtime: {} ms", methodKey, timer.getRunTime(System.currentTimeMillis()));
        return instance;
    }

}
