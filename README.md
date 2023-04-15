# rate-limiter-demo

Run DemoControllerTest.testCreate() to simulate concurrent requests for the API. Change THREAD_POOL_SIZE for concurrent size.

The test case will access DemoController.create(@RequestParam String name) to create new users with given names. @Throttling(tps = 10) is the rate limiter configured for the API.

We can change tps value to test different limiter rates. @LogRuntime helps to log the response time for each request and compare the effects of different tps values.

