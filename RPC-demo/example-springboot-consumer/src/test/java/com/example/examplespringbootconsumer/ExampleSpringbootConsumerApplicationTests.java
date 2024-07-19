package com.example.examplespringbootconsumer;

import com.example.rpcstarter.annotation.EnableRpc;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@EnableRpc(needServer = false)
class ExampleSpringbootConsumerApplicationTests {

    @Test
    void contextLoads() {
    }

}
