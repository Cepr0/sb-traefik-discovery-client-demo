package io.github.cepr0.traefik;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

@SpringBootTest(properties = {"traefik.address=localhost:8180", "traefik.provider=docker"})
@AutoConfigureWebClient
@ContextConfiguration(classes = TraefikDiscoveryClientConfiguration.class)
class TraefikDiscoveryClientTest {

    @Autowired private TraefikDiscoveryClient discoveryClient;

    @Test
    void getServices() {
        List<String> services = discoveryClient.getServices();
        System.out.println("services = " + services);
    }

    @Test
    void getInstances() {
        List<ServiceInstance> instances = discoveryClient.getInstances("demo-service");
        System.out.println("instances = " + instances);
    }
}