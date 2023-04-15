package com.leonid.demo.aspect;

import com.google.common.util.concurrent.RateLimiter;
import com.leonid.demo.annotation.LogRuntime;
import com.leonid.demo.annotation.Throttling;
import com.leonid.demo.util.Util;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Aspect
@Component
public class ThrottlingAspect {
    final Logger logger = LoggerFactory.getLogger(ThrottlingAspect.class);
    private Map<String, RateLimiter> rateLimiterMap = new ConcurrentHashMap<>();

    @Before("@annotation(com.leonid.demo.annotation.Throttling)")
    public void throttle(JoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodKey = Util.getMethodKeyNoArgs(joinPoint);
//        logger.info("methodKey: " + methodKey);
        RateLimiter rateLimiter = rateLimiterMap.get(methodKey);
        Method method = signature.getMethod();
        Throttling throttling = method.getAnnotation(Throttling.class);
        if(rateLimiter == null) {
            rateLimiter = RateLimiter.create(throttling.tps());
            rateLimiterMap.put(methodKey, rateLimiter);
        }
        rateLimiter.acquire();
//        logger.info("throttle done for " + methodKey);
    }
}
