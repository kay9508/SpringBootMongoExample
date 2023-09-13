package com.example.springbootmongoexample.document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.List;

@Document(value = "conference") // 실제 collection의 이름
@Getter
@Setter
@AllArgsConstructor
public class ConferenceDoc {

    @Id
    private ObjectId _id;

    private String confAddr;

    private String confJWT;

    private Integer confPort;

    private String confType;

    private LocalDateTime createdAt;

    private String description;

    private String estimatedTime;

    private Object host;

    private ObjectId id;

    private Integer maxMember;

    private List<Member> member;

    private String name;

    private String project;

    private String serverId;

    private String startTime;

    private String status;

    private String title;

    private LocalDateTime updatedAt;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Member {
        private String uuid;
    }
}


