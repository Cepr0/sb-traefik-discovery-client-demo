package io.github.cepr0.traefik;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class TraefikDiscoveryClient implements DiscoveryClient {

    private final TraefikDiscoveryClientProps props;
    private final RestTemplate traefikApi;

    public TraefikDiscoveryClient(TraefikDiscoveryClientProps props, RestTemplate traefikApi) {
        this.props = props;
        this.traefikApi = traefikApi;
        log.debug("[d] Traefik Discovery Client has been initialized.");
    }

    @Override
    public String description() {
        return "Traefik Discovery Client";
    }

    @Override
    public List<ServiceInstance> getInstances(String serviceId) {
        log.debug("[d] Retrieving service instances...");
        return getServiceStream()
                .filter(traefikService -> traefikService.getName().equals(serviceId + "@" + props.getProvider()))
                .flatMap(traefikService -> traefikService.getServerStatus().entrySet()
                        .stream()
                        .filter(e -> "UP".equals(e.getValue()))
                        .map(serviceAddress -> makeServiceInstance(serviceId, serviceAddress))
                ).collect(Collectors.toList());
    }

    @Override
    public List<String> getServices() {
        log.debug("[d] Retrieving service names...");
        return getServiceStream()
                .map(TraefikService::getName)
                .map(this::stripServiceName)
                .collect(Collectors.toList());
    }

    @Override
    public int getOrder() {
        return props.getOrder();
    }

    private DefaultServiceInstance makeServiceInstance(String serviceId, Map.Entry<String, String> serviceAddress) {
        URI uri = URI.create(serviceAddress.getKey());
        String host = uri.getHost();
        int port = uri.getPort() != -1 ? uri.getPort() : 80;
        return new DefaultServiceInstance(
                host.replace('.', '-') + "-" + port,
                serviceId,
                host,
                port,
                "https".equals(uri.getScheme())
        );
    }

    private String stripServiceName(String serviceName) {
        return serviceName.substring(0, serviceName.indexOf('@'));
    }

    private Stream<TraefikService> getServiceStream() {
        Stream<TraefikService> serviceStream = Stream.empty();
        try {
            ResponseEntity<TraefikService[]> response = traefikApi.getForEntity(
                    "/services?search=@{provider}&status=enabled",
                    TraefikService[].class,
                    props.getProvider()
            );
            if (response.getStatusCode() == HttpStatus.OK) {
                TraefikService[] serviceArray = response.getBody();
                if (serviceArray != null) {
                    serviceStream = Arrays.stream(serviceArray).filter(traefikService -> {
                        boolean enabled = "enabled".equals(traefikService.getStatus());
                        boolean loadBalancer = "loadbalancer".equals(traefikService.getType());
                        boolean provider = traefikService.getProvider().equals(props.getProvider());
                        boolean noTraefik = !traefikService.getName().startsWith("traefik");
                        return enabled && loadBalancer && provider && noTraefik;
                    });
                } else {
                    log.debug("[d] Traefik 'get services' response body is null");
                }
            } else {
                log.error("[!] Traefik 'get services' response status is {}, {}", response.getStatusCodeValue(), response.getBody());
            }
        } catch (ResourceAccessException e) {
            log.error("[!] Traefik service is unreachable", e);
        }
        return serviceStream;
    }
}
