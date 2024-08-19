package com.web.back.clients;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class BaseWebClient {
    private static final long CONNECT_TIMEOUT_MILLIS = 60;
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
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int)TimeUnit.SECONDS.toMillis(CONNECT_TIMEOUT_MILLIS))
                .responseTimeout(Duration.ofSeconds(CONNECT_TIMEOUT_MILLIS))
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(CONNECT_TIMEOUT_MILLIS, TimeUnit.SECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(CONNECT_TIMEOUT_MILLIS, TimeUnit.SECONDS)));

        return WebClient.builder()
                .exchangeStrategies(ExchangeStrategies.builder().codecs(
                                clientCodecConfigurer ->
                                        clientCodecConfigurer.defaultCodecs().maxInMemorySize(10000000)).build())
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
