package com.example.springbootmongoexample.mongoRepository2;

import com.example.springbootmongoexample.document.ConferenceDoc;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConferenceDocRepository extends MongoRepository<ConferenceDoc, ObjectId> {

    List<ConferenceDoc> findByMemberContains(String uuid); //이건 안되네
    List<ConferenceDoc> findByMemberContains(ConferenceDoc.Member memeber);

}
