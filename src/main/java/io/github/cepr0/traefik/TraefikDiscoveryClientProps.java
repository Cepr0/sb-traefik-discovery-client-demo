package io.github.cepr0.traefik;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.client.discovery.DiscoveryClient;

@Data
@ConfigurationProperties("traefik")
public class TraefikDiscoveryClientProps {

    /**
     * Order of this DiscoveryClient, defaults to DiscoveryClient.HIGHEST_PRECEDENCE.
     */
    private int order = DiscoveryClient.HIGHEST_PRECEDENCE;

    /**
     * Base URL of traefik HTTP services endpoint.
     */
    private String baseUrl = "http://traefik:8080/api/http";

    /**
     * Traefik provider.
     */
    private String provider = "docker";
}
