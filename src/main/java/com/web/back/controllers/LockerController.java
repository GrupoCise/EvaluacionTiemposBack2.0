package com.web.back.controllers;

import com.web.back.model.dto.WorkLockDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.JedisPooled;

import java.util.Objects;

@RestController
@RequestMapping("/lock")
@Tag(name = "Locker")
public class LockerController {
    private final String LOCK_VALUE = "locked";

    private final String RedisHost;
    private final Integer RedisPort;

    public LockerController(@Value("${REDIS-HOST}") String redisHost,
                            @Value("${REDIS-PORT}") Integer redisPort
                            ){
        this.RedisHost = redisHost;
        this.RedisPort = redisPort;
    }

    @PostMapping()
    public ResponseEntity<Boolean> createLock(WorkLockDto workLock) {
        try (JedisPooled jedis = new JedisPooled(RedisHost, RedisPort)) {

            jedis.set(workLock.toString(), LOCK_VALUE);

            return ResponseEntity.ok(true);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping()
    public ResponseEntity<Boolean> removeLock(WorkLockDto workLock) {
        try (JedisPooled jedis = new JedisPooled(RedisHost, RedisPort)) {

            jedis.del(workLock.toString());

            return ResponseEntity.ok(true);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping()
    public ResponseEntity<Boolean> isLockActive(WorkLockDto workLock) {
        try (JedisPooled jedis = new JedisPooled(RedisHost, RedisPort)) {

            var lock = jedis.get(workLock.toString());

            return ResponseEntity.ok(Objects.equals(lock, LOCK_VALUE));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
