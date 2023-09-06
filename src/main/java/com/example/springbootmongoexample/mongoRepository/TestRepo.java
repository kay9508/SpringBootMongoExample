package com.example.springbootmongoexample.mongoRepository;

import com.example.springbootmongoexample.document.EventDoc;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TestRepo extends MongoRepository<EventDoc, String> {
}
