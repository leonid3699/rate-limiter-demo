package com.leonid.demo.controller;

import com.leonid.demo.DemoApplication;
import com.leonid.demo.util.Util;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DemoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class DemoControllerTest extends TestCase {
    final Logger logger = LoggerFactory.getLogger(DemoControllerTest.class);

    public class CreateUserTask implements Callable<String> {
        final String url = "http://localhost/demo/user";
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

    @Test
    public void testCreate() throws Exception {
        final int THREAD_POOL_SIZE = 50;
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        List<Callable<String>> threadList = new LinkedList<>();
        for (int i = 0; i < THREAD_POOL_SIZE * 3; i++) {
            threadList.add(new CreateUserTask("user" + i));
        }
        executorService.invokeAll(threadList);
        executorService.shutdown();
    }
}