package com.ankit.bookstore.services;

import com.ankit.bookstore.models.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BooksRepository extends JpaRepository<Book, Integer> {
}
