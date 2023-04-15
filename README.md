# rate-limiter-demo

Run DemoControllerTest.testCreateUser() to simulate concurrent requests for the API. Change THREAD_POOL_SIZE for concurrent size.

The test case will access DemoController.create(@RequestParam String name) to create new users with given names and save to H2 memory DB. 

@Throttling(tps = 1) is the rate limiter configured for the API.

We can change tps value to test different limiter rates. 

Final logs of DemoControllerTest.testCreateUser() tells us length of time for calling the API and how many responses are success out of
the total. 
For example, with tps = 1, THREAD_POOL_SIZE = 50, TIMES = 15, logs are below

total runtime: 19773 ms
total HTTP request 750, success 18

We can set global config property application.throttlingDisabled=true to disable rate limiter feature.
