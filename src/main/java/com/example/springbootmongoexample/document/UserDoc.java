package com.example.springbootmongoexample.document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Document(value = "user") //value는 실제 collections 안에서의 테이블(컬렉선)의 이름
@Getter
@Setter
@AllArgsConstructor
public class UserDoc {

    @Id
    private String id;

    private String name;

    private String phoneNumber;

    @CreatedDate
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDate;

}
