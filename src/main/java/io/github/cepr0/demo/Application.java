package io.github.cepr0.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@EnableScheduling
@RestController
@RequestMapping("demo")
@EnableConfigurationProperties(AppProps.class)
@SpringBootApplication
public class Application {

    @Value("${spring.cloud.client.hostname}")
    private String hostname;

    private final AppProps props;
    private final RestTemplateBuilder restTemplateBuilder;

    public Application(AppProps props, RestTemplateBuilder restTemplateBuilder) {
        this.props = props;
        this.restTemplateBuilder = restTemplateBuilder;
    }

    @LoadBalanced
    @Bean
    public RestTemplate demoApi() {
        return restTemplateBuilder
                .rootUri(props.getExternalUrl())
                .defaultHeader(ACCEPT, APPLICATION_JSON_VALUE)
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @GetMapping
    public Map<String, String> get() {
        return Map.of("service", props.getServiceName() + ":" + hostname);
    }

    @Scheduled(initialDelay = 5_000, fixedRate = 5_000)
    public void callExternalService() {
        Map<?, ?> result = demoApi().getForObject("/demo", Map.class);
        log.info("[i] {} has received data from {}", props.getServiceName(), result);
    }
}
