package com.romulopereira.controller;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.romulopereira.model.Book;
import com.romulopereira.repository.BookRepository;
import com.romulopereira.response.Cambio;

@RestController
@RequestMapping("book-service")
public class BookController {
	
	@Autowired
	private Environment environment;
	
	@Autowired
	private BookRepository repository;

	// http://localhost:8100/book-service/1/BRL
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
	}
}
