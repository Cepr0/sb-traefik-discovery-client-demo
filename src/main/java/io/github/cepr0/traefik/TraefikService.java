package io.github.cepr0.traefik;

import lombok.Data;

import java.util.Map;

@Data
public class TraefikService {
    private String name;
    private String provider;
    private String type;
    private String status;
    private Map<String, String> serverStatus;
}
