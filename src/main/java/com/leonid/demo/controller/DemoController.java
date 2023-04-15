package com.leonid.demo.controller;

import com.leonid.demo.annotation.LogRuntime;
import com.leonid.demo.annotation.Throttling;
import com.leonid.demo.model.User;
import com.leonid.demo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;

@RestController
@RequestMapping(path = "/demo")
public class DemoController {
    final Logger logger = LoggerFactory.getLogger(DemoController.class);
    @Autowired
    private UserRepository userRepository;

    @RequestMapping(path = "/hello/{name}", method = RequestMethod.GET)
    @LogRuntime
    @Throttling(tps = 5)
    public String sayHello(@PathVariable String name) {
        return "hello " + name;
    }

    @RequestMapping(path = "/user/{id}", method = RequestMethod.GET)
    public User findById(@PathVariable Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @RequestMapping(path = "/user", method = RequestMethod.POST)
    @Throttling(tps = 1)
    public Long createUser(@RequestParam String name) {
        User user = new User();
        user.setName(name);
        return userRepository.save(user).getId();
    }

    @PostConstruct
    private void createDemoUser() {
        User user = new User();
        user.setName("test user");
        userRepository.save(user);
        logger.info("test user create with id: " + user.getId());
    }
}
