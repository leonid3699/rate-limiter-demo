package com.leonid.demo.util;

import com.leonid.demo.controller.DemoController;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;

public class Util {
    final static Logger logger = LoggerFactory.getLogger(DemoController.class);

    static public String getMethodKey(JoinPoint joinPoint) {
        Method method = ((MethodSignature) joinPoint.getSignature() ).getMethod();
        return Util.getMethodKey(method, joinPoint.getArgs() );
    }

    static public String getMethodKeyNoArgs(JoinPoint joinPoint) {
        Method method = ((MethodSignature) joinPoint.getSignature() ).getMethod();
        String methodKey = method.getReturnType().getSimpleName() + " " + method.getDeclaringClass().getSimpleName() + "." + method.getName() + "()";
        return methodKey;
    }

    static public String getMethodKey(Method method, Object[] args) {
        String methodKey = method.getReturnType().getSimpleName() + " " + method.getDeclaringClass().getSimpleName() + "." + method.getName() + "(";
        if(args != null && args.length > 0) {
            for(Object one : args) {
                if(one instanceof Date) {
                    methodKey += ((Date)one).getTime();
                }
                else {
                    methodKey += one.toString();
                }
                methodKey += ",";
            }
            methodKey = methodKey.substring(0, methodKey.length() - 1);
        }
        methodKey += ")";
        return methodKey;
    }

    static public String sendPOST(String url, Map<String, String> data) {
        try {
            return Jsoup.connect(url).userAgent("Mozilla").data(data).ignoreContentType(true).method(Connection.Method.POST).execute().body();
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        }
        return null;
    }

    static public String sendGET(String url) throws IOException {
        return Jsoup.connect(url).ignoreContentType(true).timeout(5 * 1000).execute().body();
    }
}
