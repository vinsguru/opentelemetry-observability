package com.vinsguru.movie.config;

import com.vinsguru.movie.client.ActorClient;
import com.vinsguru.movie.client.ReviewClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ApplicationConfiguration {
    
    @Bean
    public ActorClient actorClient(RestClient.Builder builder, @Value("${actor-service.url}") String baseUrl) {
        var client = builder.baseUrl(baseUrl).build();
        return new ActorClient(client);
    }

    @Bean
    public ReviewClient reviewClient(RestClient.Builder builder, @Value("${review-service.url}") String baseUrl) {
        var client = builder.baseUrl(baseUrl).build();
        return new ReviewClient(client);
    }

}
