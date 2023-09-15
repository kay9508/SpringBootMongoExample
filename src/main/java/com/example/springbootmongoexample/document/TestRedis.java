package com.example.springbootmongoexample.document;

import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.List;

@RedisHash("test")
@AllArgsConstructor
public class TestRedis implements Serializable {

    @Id
    private String id;
    private String userId;
    private String callStatus;
    private String registeredAt;
    private String updatedAt;
    private List<String> voipFunction1;
    private String voipFunction2;
    private String voipFunction3;
    private String voipFunction4;
    private String voipFunction5;
    private String voipFunction6;
    private String voipFunction7;
    private String voipFunction8;
    private String voipFunction9;
    private String voipFunction10;
    private String voipFunction11;
    private String voipFunction12;
    private String voipFunction13;
    private String voipFunction14;
    private String voipFunction15;
    private String voipFunction16;
    private String voipFunction17;
    private String voipFunction18;
    private String voipFunction19;
    private String voipFunction20;
    private String hostName;
    private String phoneNumber;
    private String customer;
    private String serviceType;
    private String regiStatus;
    private String smsStatus;
    private String smsMaxLength;
    private String autoAnswer;
    private String fwdNumber;
    private String pickupCode;
    private String callDevice;
    private String phoneDisplayName;
}
