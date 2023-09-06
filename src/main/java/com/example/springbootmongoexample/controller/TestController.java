package com.example.springbootmongoexample.controller;

import com.example.springbootmongoexample.document.BookDoc;
import com.example.springbootmongoexample.document.EventDoc;
import com.example.springbootmongoexample.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TestController {

    // Document 별로 service를 미리 정해두고 내부의 메서드를 통해 사용할 수 있음
    @Autowired
    EventService eventService;

    // 직접 MongoTemplate 을 이용해서 사용할 경우에
    @Autowired
    private MongoTemplate mongoTemplate;

    @GetMapping(path = "/test")
    public ResponseEntity<String> chattingPushFcm(EventDoc eventDoc) {
        eventService.insertEvent(eventDoc);

        BookDoc bookDoc = new BookDoc("책2", "나의라임오렌지나무", "대충 나의라임오렌지나무의 내용");
        mongoTemplate.insert(bookDoc);


        /* 몽고 템플릿을 사용한 검색 TODO 근데 셋다 결과가 안나오는데?*/
        Query query = new Query().addCriteria(Criteria.where("title").is(".*어린*."));
        List<BookDoc> findBooks = mongoTemplate.find(query, BookDoc.class);

        Query query2 = new Query().addCriteria(Criteria.where("title").is("어린"));
        List<BookDoc> findBooks2 = mongoTemplate.find(query, BookDoc.class);

        Query query3 = new Query().addCriteria(Criteria.where("title").is("어린왕자"));
        List<BookDoc> findBooks3 = mongoTemplate.find(query, BookDoc.class);

        return ResponseEntity.ok("ok");
    }
}
