package com.leonid.demo.controller;

import com.leonid.demo.DemoApplication;
import com.leonid.demo.annotation.LogRuntime;
import com.leonid.demo.aspect.LogRuntimeAspect;
import com.leonid.demo.util.Util;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DemoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class DemoControllerTest extends TestCase {
    final Logger logger = LoggerFactory.getLogger(DemoControllerTest.class);
    @Value("${server.port:8080}")
    private int port;

    public class CreateUserTask implements Callable<String> {
        final String url = "http://localhost:" + port + "/demo/user";
        private String name;
        public CreateUserTask(String name) {
            this.name = name;
        }
        @Override
        public String call() throws Exception {
            Map<String, String> data = new HashMap<>();
            data.put("name", name);
//            logger.info("calling " + url);
            return Util.sendPOST(url, data);
        }
    }

    private int invoke(ExecutorService executorService, List<Callable<String>> threadList) throws Exception {
        int success = 0;
        for (Future<String> future : executorService.invokeAll(threadList)) {
            String got = future.get();
            if(got != null) {
                success++;
//                logger.info("response: " + got);
            }
        }
        executorService.shutdown();
        return success;
    }

    @Test
    public void testCreateUser() throws Exception {
        LogRuntimeAspect.Timer timer = new LogRuntimeAspect.Timer(System.currentTimeMillis());
        final int THREAD_POOL_SIZE = 50;
        final int TIMES = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        List<Callable<String>> threadList = new LinkedList<>();
        int total = 0;
        int success = 0;
        //give rate limiter time to warm up
        Thread.sleep(1000);
        for (int i = 0; i < THREAD_POOL_SIZE * TIMES; i++) {
            total++;
            threadList.add(new CreateUserTask("user" + i));
            if(i % THREAD_POOL_SIZE == THREAD_POOL_SIZE - 1) {
//                logger.info("i: " + i);
                success += invoke(executorService, threadList);
                executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
                threadList.clear();
                //send requests in every second
                Thread.sleep(1000);
            }
        }
        logger.info("total runtime: {} ms", timer.getRunTime(System.currentTimeMillis()));
        logger.info("total HTTP request {}, success {}", total, success);
    }
}