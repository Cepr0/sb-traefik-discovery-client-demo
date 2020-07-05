package io.github.cepr0.traefik;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableDiscoveryClient(autoRegister = false)
@EnableConfigurationProperties(TraefikDiscoveryClientProps.class)
public class TraefikDiscoveryClientConfiguration {

    private final TraefikDiscoveryClientProps props;

    public TraefikDiscoveryClientConfiguration(TraefikDiscoveryClientProps props) {
        this.props = props;
    }

    @Bean
    public DiscoveryClient traefikDiscoveryClient(RestTemplateBuilder restTemplateBuilder) {
        return new TraefikDiscoveryClient(props, restTemplateBuilder);
    }
}
