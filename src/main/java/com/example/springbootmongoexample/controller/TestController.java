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

        //몽고 템플릿을 사용한 검색 TODO 근데 셋다 결과가 안나오는데?
        Query query = new Query().addCriteria(Criteria.where("title").is(".*어린*."));
        List<BookDoc> findBooks = primaryMongoTemplate.find(query, BookDoc.class);

        Query query2 = new Query().addCriteria(Criteria.where("title").is("어린"));
        List<BookDoc> findBooks2 = primaryMongoTemplate.find(query, BookDoc.class);

        Query query3 = new Query().addCriteria(Criteria.where("title").is("어린왕자"));
        List<BookDoc> findBooks3 = primaryMongoTemplate.find(query, BookDoc.class);

        Query query4 = new Query().addCriteria(Criteria.where("title").is("^어린"));
        List<BookDoc> findBooks4 = primaryMongoTemplate.find(query, BookDoc.class);

        // 전체 조회
        List<BookDoc> all = primaryMongoTemplate.findAll(BookDoc.class);

        bookDocRepository.save(new BookDoc("아이디", "제목2132", "내용입니다."));
        BookDoc load1 = bookDocRepository.findByTitle("제");
        BookDoc load2 = bookDocRepository.findByTitle("제목2132");
        List<BookDoc> load3 = bookDocRepository.findByTitleIsLike("제");


        return ResponseEntity.ok("ok");
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

    @GetMapping("/callLog/{_id}") //이거 받아지지가 않네? String 으로받아서 변환해야하나...
    public ResponseEntity<CallLogDoc> searchCallLogDoc(CallLogDoc callLogDoc) {

        ObjectId test = new ObjectId("64f96a6235129d6b0b1c9639");
        callLogDoc.set_id(test);
        //이거 왜 load를 못하지?
        Optional<CallLogDoc> loadOptional = callLogDocRepository.findById(callLogDoc.get_id());
        CallLogDoc load = null;
        if (loadOptional.isPresent()) {
            load = loadOptional.get();
        }

        List<CallLogDoc> all = callLogDocRepository.findAll();

//        Query query = new Query().addCriteria(Criteria.where("_id").is("어린왕자"));
//        List<BookDoc> findBooks3 = primaryMongoTemplate.find(query, BookDoc.class);
//        secondaryMongoTemplate.find()

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


        return ResponseEntity.ok().body(load);
    }
}
