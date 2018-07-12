package com.kenzan.canary.bookservice.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BookDto {
    private String id;
    private String title;
    private String author;
    private List<String> category;

    @JsonCreator
    public BookDto(@JsonProperty("id") String id,
    @JsonProperty("title") String title,
    @JsonProperty("author") String author,
    @JsonProperty("category") List<String> category) {

        this.id = id;
        this.title = title;
        this.author = author;
        this.category = category;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return the category
     */
    public List<String> getCategory() {
        return category;
    }

    /**
     * @return the author
     */
    public String getAuthor() {
        return author;
    }
}