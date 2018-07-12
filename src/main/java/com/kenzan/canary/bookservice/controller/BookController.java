package com.kenzan.canary.bookservice.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import com.codahale.metrics.annotation.Timed;
import com.kenzan.canary.bookservice.dto.BookDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.prometheus.client.Counter;


@RestController
public class BookController {

    @Autowired()
    @Qualifier("getHttpRequestsTotalCounter")
    private Counter getBooksCounter;

    @RequestMapping(value="/books", method = RequestMethod.GET)
    @Timed(name="getBooksTimer")
    public ResponseEntity<List<BookDto>> getBook() {
        getBooksCounter.labels("getBooks", "GET", "500").inc();
        List<BookDto> books = new ArrayList<>();

        books.add(new BookDto("1", "Book One", "me", Arrays.asList("horror")));

        return ResponseEntity.status(500).build();
    }
}