package com.example.springbootmongoexample.document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(value = "call_log") //value는 실제 collections 안에서의 테이블(컬렉선)의 이름
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CallLogDoc {
    @Id
    private ObjectId _id;
    private LocalDateTime createdAt;
    private String devType;
    private String display;
    private String endTime;
    private String no;
    private String status;
    private String time;
    private String type;
    private LocalDateTime updatedAt;
    private String userId;
}
