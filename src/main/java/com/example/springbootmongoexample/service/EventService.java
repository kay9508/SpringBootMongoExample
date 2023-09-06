package com.example.springbootmongoexample.service;

import com.example.springbootmongoexample.document.EventDoc;
import com.example.springbootmongoexample.exception.RestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public EventDoc getEvent(String _id) {
        EventDoc event = mongoTemplate.findById(_id, EventDoc.class);
        return Optional.ofNullable(event).orElseThrow(() -> new RestException(HttpStatus.NOT_FOUND, "Not found event"));
    }

    public List<EventDoc> getEventList(String title) {
        Query query = new Query().addCriteria(Criteria.where("title").is(title));
        return mongoTemplate.find(query, EventDoc.class);
    }

    public EventDoc insertEvent(EventDoc body) {
        return mongoTemplate.insert(body);
    }

}
