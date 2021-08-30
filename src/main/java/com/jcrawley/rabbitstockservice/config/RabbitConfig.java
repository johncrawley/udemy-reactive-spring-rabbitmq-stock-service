package com.jcrawley.rabbitstockservice.config;

import javax.annotation.PreDestroy;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.rabbitmq.client.Connection;

import reactor.core.publisher.Mono;
import reactor.rabbitmq.RabbitFlux;
import reactor.rabbitmq.Sender;
import reactor.rabbitmq.SenderOptions;

@Configuration
public class RabbitConfig {
		
	public static final String QUEUE = "quotes";
	
	@Autowired
	Mono<Connection> connectionMono;
	
	@Bean
	Mono<Connection> connectioMono(CachingConnectionFactory connectionFactory){
		return Mono.fromCallable(() -> connectionFactory.getRabbitConnectionFactory().newConnection());
	}
	
	@PreDestroy
	public void close() throws Exception {
		connectionMono.block().close();
	}
	
	@Bean
	Sender sender(Mono<Connection> mono) {
		return RabbitFlux.createSender(new SenderOptions().connectionMono(mono));
	}
}
