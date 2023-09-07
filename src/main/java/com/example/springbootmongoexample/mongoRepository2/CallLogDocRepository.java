package com.example.springbootmongoexample.mongoRepository2;

import com.example.springbootmongoexample.document.CallLogDoc;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * 이게 같은 패키지 안에있으면 다른 BD로 인식을 안해서 이미 선언되어있다고 나오는 문제가 있음
 * 그래서 mongoRepository2 라는 패키지를 만들었다....
 */
@Repository
public interface CallLogDocRepository extends MongoRepository<CallLogDoc, ObjectId> {

}
