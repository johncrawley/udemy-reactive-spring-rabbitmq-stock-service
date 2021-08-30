package com.jcrawley.rabbitstockservice.service;

import java.time.Duration;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class QuoteRunner implements CommandLineRunner {

	private final QuoteGeneratorService quoteGeneratorService;
	private final QuoteMessageSender quoteMessageSender;
	
	@Override
	public void run(String... args) throws Exception {
		
			quoteGeneratorService.fetchQuoteStream(Duration.ofMillis(200))
			.take(25)
			.log("Got Quote")
			.flatMap(quoteMessageSender::sendQuoteMessage)
			.subscribe( result ->  log.debug("Sent Message to Rabbit"),
					throwable -> log.debug("Error trying to send message"),
					() -> log.debug("Finished sending messages.")
			);
	}

}
