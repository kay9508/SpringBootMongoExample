package com.example.springbootmongoexample.mongoRepository;

import com.example.springbootmongoexample.document.BookDoc;
import com.example.springbootmongoexample.document.UserDoc;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
//QuerydslPredicateExecutor<Document>은 queryDsl사용시에만 추가
//public interface UserDocRepository extends MongoRepository<UserDoc, String>, QuerydslPredicateExecutor<UserDoc> {
public interface UserDocRepository extends MongoRepository<UserDoc, String> {
    BookDoc findByName(String name);
}
