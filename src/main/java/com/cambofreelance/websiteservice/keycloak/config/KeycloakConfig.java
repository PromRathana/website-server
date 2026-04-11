package com.cambofreelance.websiteservice.keycloak.config;

import com.cambofreelance.websiteservice.keycloak.KeycloakProperties;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
@RequiredArgsConstructor
public class KeycloakConfig {

    private final KeycloakProperties props;

    @Bean("keycloakWebClient")
    public WebClient keycloakWebClient(WebClient.Builder builder) {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, props.getHttp().getConnectTimeoutMs())
                .responseTimeout(Duration.ofMillis(props.getHttp().getResponseTimeoutMs()))
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(props.getHttp().getResponseTimeoutMs(), TimeUnit.MILLISECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(props.getHttp().getResponseTimeoutMs(), TimeUnit.MILLISECONDS)));

        return builder
                .baseUrl(props.getUrl())
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(c -> c.defaultCodecs().maxInMemorySize(4 * 1024 * 1024)).build())
                .build();
    }
}
