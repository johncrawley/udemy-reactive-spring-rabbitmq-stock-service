package com.jcrawley.rabbitstockservice.service;

import java.time.Duration;

import com.jcrawley.rabbitstockservice.model.Quote;

import reactor.core.publisher.Flux;

public interface QuoteGeneratorService {

	Flux<Quote> fetchQuoteStream(Duration period);
	
}
