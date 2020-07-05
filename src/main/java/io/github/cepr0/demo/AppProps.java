package io.github.cepr0.demo;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;

@Data
@Validated
@ConfigurationProperties("demo")
public class AppProps {
    @NotEmpty private String externalUrl;
    @NotEmpty private String serviceName;
}
