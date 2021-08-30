package com.jcrawley.reactiverabbitmqstockservice.service;

import java.time.Duration;

import com.jcrawley.reactiverabbitmqstockservice.model.Quote;

import reactor.core.publisher.Flux;

public interface QuoteGeneratorService {

	Flux<Quote> fetchQuoteStream(Duration period);
	
}
