package com.example.springbootmongoexample.controller;

import com.example.springbootmongoexample.document.*;
import com.example.springbootmongoexample.mongoRepository.BoardDocRepository;
import com.example.springbootmongoexample.mongoRepository2.BookDocRepository;
import com.example.springbootmongoexample.mongoRepository2.CallLogDocRepository;
import com.example.springbootmongoexample.mongoRepository.UserDocRepository;
import com.example.springbootmongoexample.mongoRepository2.ConferenceDocRepository;
import com.example.springbootmongoexample.redisRepository.TestRedisRepository;
import com.example.springbootmongoexample.service.EventService;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
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

    @Autowired
    private TestRedisRepository testRedisRepository;

    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate redisTemplate;

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

        /*TestRedis saveTest = new TestRedis(
                "IP460S_a8e539b7dbac",
                "705883c5-9ced-4569-ac97-d850911d91b7",
                "idle",
                "2023-09-13T04:07:41.341Z",
                "2023-09-13T07:29:13.479Z",
                List.of("disable","always"),
                "disable",
                "01096415737",
                "0",
                "disable",
                "disable",
                "disable",
                "",
                "disable",
                "disable",
                "IPC_I_MS_460S_2.3.108",
                "enable",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "58.181.23.160",
                "07045131599",
                "KCT",
                "2",
                "ok",
                "ok",
                "80",
                "disable",
                "01096415737",
                "**",
                "",
                ""
        );*/

        //redisTemplate.opsForValue().set("705883c5-9ced-4569-ac97-d850911d91b7", "rd");
        Object load2 = redisTemplate.opsForValue().get("IP460S_a8e539b81b7c");
        Object load9 = redisTemplate.opsForValue().get(".IP460S");

        // https://medium.com/@chlee7746/redis-scan-%EB%AA%85%EB%A0%B9%EC%96%B4-%ED%8D%BC%ED%8F%AC%EB%A8%BC%EC%8A%A4-e29e242b8038

        Object load19 = redisTemplate.keys(".IP");
        Object load119 = redisTemplate.keys("IP*");


        Optional<TestRedis> loadTestRedis = testRedisRepository.findById("IP460S_a8e539b81b7c");
        Optional<TestRedis> loadTestRedis2 = testRedisRepository.findById("IP460S_a8e539b81b7c");


        /*ScanOptions scanOptions = ScanOptions.scanOptions().match("*").count(10).build();
        Cursor<byte[]> keys = redisTemplate.getConnectionFactory().getConnection().scan(scanOptions);*/

        RedisConnection redisConnection = redisTemplate.getConnectionFactory().getConnection();
        ScanOptions options = ScanOptions.scanOptions().match("705883c5-9ced-4569-ac97-d850911d91b7-2295*").count(400).build();

        Cursor<byte[]> c = redisConnection.scan(options);
        while (c.hasNext()) {
            log.info(new String(c.next()));
        }


        /*for (Integer i = 0; i < 100000; i++) {
            redisTemplate.opsForValue().set("705883c5-9ced-4569-ac97-d850911d91b7-" + i.toString(), i.toString());
        }*/

        /*TestRedis testRedis = new TestRedis(
                "IP460S_a8e539b81b7c1111",
                "6ea7433f-8ff7-42d8-9a1b-91a113aa14cf",
                null,
                "2023-09-12T07:50:09.387Z",
                "2023-09-13T06:09:08.811Z",
                null,
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "58.181.23.160",
                "07045131555",
                "KCT",
                "3",
                "ok",
                "ok",
                "80",
                "disable",
                "01089435227",
                "**",
                "",
                ""
        );

        testRedisRepository.save(testRedis);*/


        return ResponseEntity.ok().body(load);
    }
}
