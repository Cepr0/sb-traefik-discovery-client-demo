package io.github.cepr0.traefik;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Configuration
@EnableDiscoveryClient(autoRegister = false)
@EnableConfigurationProperties(TraefikDiscoveryClientProps.class)
public class TraefikDiscoveryClientConfiguration {

    private final TraefikDiscoveryClientProps props;

    public TraefikDiscoveryClientConfiguration(TraefikDiscoveryClientProps props) {
        this.props = props;
    }

    @ConditionalOnMissingBean(name = "traefikApi")
    @Bean
    public RestTemplate traefikApi(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder
                .rootUri(props.getBaseUrl())
                .defaultHeader(ACCEPT, APPLICATION_JSON_VALUE)
                .build();
    }

    @Bean
    public DiscoveryClient traefikDiscoveryClient(@Qualifier("traefikApi") RestTemplate traefikApi) {
        return new TraefikDiscoveryClient(props, traefikApi);
    }
}
