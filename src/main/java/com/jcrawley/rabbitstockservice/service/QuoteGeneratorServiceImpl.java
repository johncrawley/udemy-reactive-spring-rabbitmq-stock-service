package com.jcrawley.rabbitstockservice.service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;

import org.springframework.stereotype.Service;

import com.jcrawley.rabbitstockservice.model.Quote;

import reactor.core.publisher.Flux;
import reactor.core.publisher.SynchronousSink;

@Service
public class QuoteGeneratorServiceImpl implements QuoteGeneratorService{

	private final MathContext mathContext = new MathContext(2);
	private final Random random = new Random();
	private final List<Quote> quotes = new ArrayList<>();
	
	public QuoteGeneratorServiceImpl() {

		add("AAPL", 134.15);
		add("TSLA", 739.84);
		add("NFLX", 543.24);
		add("ARKK", 124.51);
		add("SQ", 246.34);
		add("DTS", 187.29);
		add("MFST", 258.28);
		add("PLTR", 92.21);
	}
	
	private void add(String name, Double price) {
		quotes.add(new Quote(name, price));
	}
	
	@Override
	public Flux<Quote> fetchQuoteStream(Duration period){
		
		return Flux.generate(() -> 0,
			(BiFunction<Integer, SynchronousSink<Quote>, Integer>)(index, sink) -> {
					Quote updatedQuote = updateQuote(quotes.get(index));
					sink.next(updatedQuote);
					return ++index % this.quotes.size();
				})
				.zipWith(Flux.interval(period))
				.map(t -> t.getT1())
				.map(quote -> {
					quote.setInstant(Instant.now());
					return quote;})
				.log("QuoteGeneratorService");
	}
	

	private Quote updateQuote(Quote quote) {
		BigDecimal priceChange = quote.getPrice()
				.multiply(new BigDecimal(0.05 * this.random.nextDouble()), this.mathContext);
				return new Quote(quote.getTicker(), quote.getPrice().add(priceChange));
	}
}
