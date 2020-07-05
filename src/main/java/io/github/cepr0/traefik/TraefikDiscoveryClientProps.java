package io.github.cepr0.traefik;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.client.discovery.DiscoveryClient;

@Data
@ConfigurationProperties("traefik")
public class TraefikDiscoveryClientProps {
    private int order = DiscoveryClient.HIGHEST_PRECEDENCE;
    private String address;
    private String provider;
}
