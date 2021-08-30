package com.jcrawley.rabbitstockservice.service;


import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcrawley.rabbitstockservice.config.RabbitConfig;
import com.jcrawley.rabbitstockservice.model.Quote;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.OutboundMessage;
import reactor.rabbitmq.OutboundMessageResult;
import reactor.rabbitmq.QueueSpecification;
import reactor.rabbitmq.Sender;

@Component
@Slf4j
@RequiredArgsConstructor
public class QuoteMessageSender {
	private final ObjectMapper objectMapper;
	private final Sender sender;
	
	@SneakyThrows
	public Mono<Void> sendQuoteMessage(Quote quote){
		byte[] jsonBytes = objectMapper.writeValueAsBytes(quote);
		//return sender.send(Mono.just(new OutboundMessage("", RabbitConfig.QUEUE, jsonBytes)));
		
		Flux<OutboundMessageResult> confirmations = sender.sendWithPublishConfirms(
				Flux.just(new OutboundMessage("", RabbitConfig.QUEUE, jsonBytes)));
		
		sender.declareQueue(QueueSpecification.queue(RabbitConfig.QUEUE))
			.thenMany(confirmations)
			.doOnError(e -> log.error("Send failed", e))
			.subscribe(r -> {
				if(r.isAck()) {
					log.info("Message send Successfully {}", String.valueOf(r.getOutboundMessage().getBody()));
				}
			});
		return Mono.empty();
		
	}

}
