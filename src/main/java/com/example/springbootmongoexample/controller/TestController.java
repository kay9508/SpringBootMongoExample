package com.example.springbootmongoexample.controller;

import com.example.springbootmongoexample.document.*;
import com.example.springbootmongoexample.mongoRepository.BoardDocRepository;
import com.example.springbootmongoexample.mongoRepository2.BookDocRepository;
import com.example.springbootmongoexample.mongoRepository2.CallLogDocRepository;
import com.example.springbootmongoexample.mongoRepository.UserDocRepository;
import com.example.springbootmongoexample.mongoRepository2.ConferenceDocRepository;
import com.example.springbootmongoexample.service.EventService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class TestController {

    // Document 별로 service를 미리 정해두고 내부의 메서드를 통해 사용할 수 있음
    @Autowired
    EventService eventService;

    @Autowired
    @Qualifier("secondaryMongoTemplate") //이부분이 없으면 @Primary에 의해 기본값인 primary만 연결된다.
    private MongoTemplate secondaryMongoTemplate;

    @Autowired
    @Qualifier("primaryMongoTemplate")
    private MongoTemplate primaryMongoTemplate;

    @Autowired
    private BoardDocRepository boardDocRepository;

    @Autowired
    private UserDocRepository userDocRepository;

    @Autowired
    private CallLogDocRepository callLogDocRepository;

    @Autowired
    private BookDocRepository bookDocRepository;

    @Autowired
    private ConferenceDocRepository conferenceDocRepository;

    @PostMapping(path = "/eventDoc")
    public ResponseEntity<String> insertEventDoc(EventDoc eventDoc) {
        eventService.insertEvent(eventDoc);

        BookDoc bookDoc = new BookDoc("책3", "노인과바다", "대충 노인과바다의 내용");
        primaryMongoTemplate.insert(bookDoc);

        return ResponseEntity.ok("ok");
    }

    @PostMapping(path = "/bookDoc")
    public ResponseEntity<String> insertBookDoc(BookDoc bookDoc) {
        primaryMongoTemplate.insert(bookDoc);

        return ResponseEntity.ok("ok");
    }

    @GetMapping("/bookDoc")
    public ResponseEntity<List<BookDoc>> loadBookDoc(BookDoc bookDoc) {

        // MongoTemplate을 사용한 like검색
        Query query = new Query().addCriteria(Criteria.where("title").regex(".*" + bookDoc.getTitle() +".*","i"));
        List<BookDoc> findBooks = primaryMongoTemplate.find(query, BookDoc.class);

        // MongoTemplate을 사용한 전체 조회
        List<BookDoc> all = primaryMongoTemplate.findAll(BookDoc.class);

        // Repository를 사용한 검색
        BookDoc load1 = bookDocRepository.findByTitle("제");
        BookDoc load2 = bookDocRepository.findByTitle("제목2132");
        List<BookDoc> load3 = bookDocRepository.findByTitleIsLike("제");

        return ResponseEntity.ok().body(findBooks);
    }

    @PostMapping(path = "/boardDoc")
    public ResponseEntity<String> insertBoardDoc(BoardDoc boardDoc) {
        //mongoTemplate.insert(boardDoc);

        boardDoc.setId("두번째DB");
        boardDoc.setTitle("두번째" + boardDoc.getTitle());
        boardDoc.setContents("두번째" + boardDoc.getContents());
        secondaryMongoTemplate.insert(boardDoc);

        boardDoc.setId("primaryDB");
        boardDoc.setTitle("primary" + boardDoc.getTitle());
        boardDoc.setContents("primary" + boardDoc.getContents());
        primaryMongoTemplate.insert(boardDoc);

        return ResponseEntity.ok("ok");
    }

    @PostMapping(path = "/user")
    public ResponseEntity<String> insertUserDoc(UserDoc userDoc) {
        userDocRepository.save(userDoc); //Repository를 사용한 저장방법
        return ResponseEntity.ok("ok");
    }

    @GetMapping("/boardDoc")
    public ResponseEntity<List<BoardDoc>> searchBoardDocsWithTitle(String title) {

        List<BoardDoc> find = boardDocRepository.findByTitleIsLike(title); //Repository를 사용한 검색방법

        return ResponseEntity.ok().body(find);
    }

    @GetMapping("/callLog/{_id}")
    public ResponseEntity<CallLogDoc> searchCallLogDoc(CallLogDoc callLogDoc) {

        ObjectId test = new ObjectId("64f96a6235129d6b0b1c9639");
        callLogDoc.set_id(test);

        Optional<CallLogDoc> loadOptional = callLogDocRepository.findById(callLogDoc.get_id());
        CallLogDoc load = null;
        if (loadOptional.isPresent()) {
            load = loadOptional.get();
        }

        List<CallLogDoc> all = callLogDocRepository.findAll();

        return ResponseEntity.ok().body(load);
    }

    @GetMapping("/conference/{_id}")
    public ResponseEntity<ConferenceDoc> loadConference(ConferenceDoc conferenceDoc) {

        ConferenceDoc load = conferenceDocRepository.findById(conferenceDoc.get_id()).get();

        Query query = new Query().addCriteria(Criteria.where("_id").is(conferenceDoc.get_id()));
        List<ConferenceDoc> findTest = secondaryMongoTemplate.find(query, ConferenceDoc.class);

        List<ConferenceDoc> loadByMembers = conferenceDocRepository.findAll();
        List<ConferenceDoc> loadByUuidOfMember = conferenceDocRepository.findByMemberContains("0c90e89a-f8d7-4fdf-b64d-874f5ee71bad");

        ConferenceDoc.Member t = new ConferenceDoc.Member("0c90e89a-f8d7-4fdf-b64d-874f5ee71bad");
        List<ConferenceDoc> loadByUuidOfMember2 = conferenceDocRepository.findByMemberContains(t);

        ConferenceDoc.Member t2 = new ConferenceDoc.Member("f56ae79e-eeb7-4c83-a93a-bdbd41013e83");
        List<ConferenceDoc> loadByUuidOfMember3 = conferenceDocRepository.findByMemberContains(t2);


        // MongoTemplate를 사용한 Object검색
        Query query2 = new Query().addCriteria(Criteria.where("member").in(t2));
        List<ConferenceDoc> loadByUuidOfMember4 = secondaryMongoTemplate.find(query2, ConferenceDoc.class);


        return ResponseEntity.ok().body(load);
    }
}
