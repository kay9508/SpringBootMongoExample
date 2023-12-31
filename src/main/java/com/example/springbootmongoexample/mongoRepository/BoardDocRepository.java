package com.example.springbootmongoexample.mongoRepository;

import com.example.springbootmongoexample.document.BoardDoc;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
//public interface BoardDocRepository extends MongoRepository<BoardDoc, String>, QuerydslPredicateExecutor<BoardDoc> {
//public interface BoardDocRepository extends MongoRepository<BoardDoc, String>, JpaRepository<BoardDoc, String> {
public interface BoardDocRepository extends MongoRepository<BoardDoc, String> {
    //JPARepository처럼 사용이 가능하네?
    BoardDoc findByTitle(String title);

    List<BoardDoc> findByTitleIsLike(String title);
}
