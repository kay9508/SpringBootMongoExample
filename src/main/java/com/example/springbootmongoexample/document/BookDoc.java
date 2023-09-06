package com.example.springbootmongoexample.document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(value = "book")
@Getter
@Setter
@AllArgsConstructor
public class BookDoc {

    @Id
    private String id;

    private String title;

    private String contents;

}
