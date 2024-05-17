package com.web.back.clients;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class BaseWebClient {
    private static final long CONNECT_TIMEOUT_MILLIS = 5000;
    private String baseUrl;

    public static BaseWebClient builder() {
        return new BaseWebClient();
    }

    public BaseWebClient baseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    public WebClient build() {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) CONNECT_TIMEOUT_MILLIS)
                .responseTimeout(Duration.ofMillis(CONNECT_TIMEOUT_MILLIS))
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)));

        return WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
