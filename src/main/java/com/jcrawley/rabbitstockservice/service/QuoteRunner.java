package com.jcrawley.rabbitstockservice.service;


import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.jcrawley.rabbitstockservice.config.RabbitConfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.rabbitmq.Receiver;

@Slf4j
@Component
@RequiredArgsConstructor
public class QuoteRunner implements CommandLineRunner {

	private final QuoteGeneratorService quoteGeneratorService;
	private final QuoteMessageSender quoteMessageSender;
	//private final Flux<Delivery> deliveryFlux;
	private final Receiver receiver;
	
	@Override
	public void run(String... args) throws Exception {
		
			CountDownLatch countDownLatch = new CountDownLatch(1);
		
			quoteGeneratorService.fetchQuoteStream(Duration.ofMillis(200))
			.take(25)
			.log("Got Quote")
			.flatMap(quoteMessageSender::sendQuoteMessage)
			.subscribe( result -> {  
				log.debug("Sent Message to Rabbit");
				countDownLatch.countDown();
			},
					throwable -> log.debug("Error trying to send message"),
					() -> log.debug("Finished sending messages.")
			);
			
			countDownLatch.await(1, TimeUnit.SECONDS);
			AtomicInteger receivedCount = new AtomicInteger();
			
			receiver.consumeAutoAck(RabbitConfig.QUEUE)
				.log("Msg Receiver")
				.subscribe(msg -> {
					log.debug("Received Message #{} - {}",
							receivedCount.incrementAndGet(), 
							String.valueOf(msg.getBody()));},
						throwable -> {},
						() -> { log.debug("Complete"); });
			
			/*
			deliveryFlux.subscribe( msg ->{
				log.debug("Received Message #{} - {}",
						receivedCount.incrementAndGet(), 
						String.valueOf(msg.getBody()));
			});
			
			*/
			
			
			
			
	}

}
