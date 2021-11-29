package com.romulopereira.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.romulopereira.model.Book;

public interface BookRepository extends JpaRepository<Book, Long>{

}
