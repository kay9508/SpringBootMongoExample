package com.example.springbootmongoexample.document;

//import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Document(value = "board") //value는 실제 collections 안에서의 테이블(컬렉선)의 이름
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
//@Entity// @Entity를 명시해야 Querydsl의 Q 엔티티 클래스가 생성됨 (spring-data-jpa의 어노테이션)
public class BoardDoc {

    @Id
    private String id;

    private String title;

    private String contents;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDate;

}
