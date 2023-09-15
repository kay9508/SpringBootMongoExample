package com.example.springbootmongoexample.redisRepository;

import com.example.springbootmongoexample.document.TestRedis;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestRedisRepository extends CrudRepository<TestRedis, String> {

}
