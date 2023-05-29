package me.krutikov.redisconnector.redis;

import lombok.NonNull;
import redis.clients.jedis.Jedis;

import java.util.Set;

public interface IRedis {
    void authenticateJedis(@NonNull Jedis jedis);
    void setValue(String key, String value);

    String getValue(String targetKey);

    void pub(String channel, String message);

    void HashSetValue(String key, String field, String value);

    String HashGetValue(String targetKey, String field);

    Set<String> getArray(String key);

    void addToArray(String key, String value);

    void removeFromArray(String key, String value);

    void removeKey(String key);
}
