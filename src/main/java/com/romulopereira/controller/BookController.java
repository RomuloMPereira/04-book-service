package com.romulopereira.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.romulopereira.model.Book;
import com.romulopereira.proxy.CambioProxy;
import com.romulopereira.repository.BookRepository;

@RestController
@RequestMapping("book-service")
public class BookController {
	
	@Autowired
	private Environment environment;
	
	@Autowired
	private BookRepository repository;
	
	@Autowired
	private CambioProxy proxy;

	// http://localhost:8100/book-service/1/BRL
	@GetMapping(value = "/{id}/{currency}")
	public Book findBook(
				@PathVariable("id") Long id,
				@PathVariable("currency") String currency
			) {
		var book = repository.getById(id);
		
		if(book == null) throw new RuntimeException("Book not found");
		
		var cambio = proxy.getCambio(book.getPrice(), "USD", currency);
		
		var port = environment.getProperty("local.server.port");
		
		book.setPrice(cambio.getConvertedValue());
		book.setEnvironment(port);
		
		return book;
	}
	
	/**
	@GetMapping(value = "/{id}/{currency}")
	public Book findBook(
				@PathVariable("id") Long id,
				@PathVariable("currency") String currency
			) {
		var book = repository.getById(id);
		
		if(book == null) throw new RuntimeException("Book not found");
		
		HashMap<String, String> params = new HashMap<>();
		params.put("amount", book.getPrice().toString());
		params.put("from", "USD");
		params.put("to", currency);
		
		var response = new RestTemplate().getForEntity("http://localhost:8000/cambio-service/"
				+ "{amount}/{from}/{to}", 
				Cambio.class, params);
		var cambio = response.getBody();
		book.setPrice(cambio.getConvertedValue());
		
		var port = environment.getProperty("local.server.port");
		book.setEnvironment(port);
		
		return book;
	} **/
}
