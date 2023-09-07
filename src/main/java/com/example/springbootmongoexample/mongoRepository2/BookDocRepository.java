package com.example.springbootmongoexample.mongoRepository2;

import com.example.springbootmongoexample.document.BookDoc;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
//QuerydslPredicateExecutor<Document>은 queryDsl사용시에만 추가
//public interface BookDocRepository extends MongoRepository<BookDoc, String>, QuerydslPredicateExecutor<BookDoc> {
public interface BookDocRepository extends MongoRepository<BookDoc, String> {
    BookDoc findByTitle(String title);
    List<BookDoc> findByTitleIsLike(String title);
}
