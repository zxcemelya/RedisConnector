package me.krutikov.redisconnector.redis.impl;

import lombok.NonNull;

import me.krutikov.redisconnector.Main;
import me.krutikov.redisconnector.redis.IRedis;
import me.krutikov.redisconnector.redis.constants.ConnectionConstants;
import net.md_5.bungee.api.ProxyServer;
import redis.clients.jedis.Jedis;

import java.util.Set;

public class RedisService implements IRedis {


    public static Jedis newJedisInstance() {
        return new Jedis(ConnectionConstants.DOMAIN.getValue(), 6379);
    }

    public void authenticateJedis(@NonNull Jedis jedis) {
        jedis.auth(ConnectionConstants.PASSWORD.getValue());
    }

    @Override
    public void setValue(String key, String value) {
        Main.getInstance().getProxy().getScheduler().runAsync(Main.getInstance(), () -> {
            try (Jedis jedis = RedisService.newJedisInstance()) {
                authenticateJedis(jedis);
                jedis.set(key, value);
            }
        });
    }

    @Override
    public String getValue(String targetKey) {
        String key;
        try (Jedis jedis = newJedisInstance()) {
            authenticateJedis(jedis);
            key = jedis.get(targetKey);

        }
        return key;
    }

    @Override
    public void pub(String channel, String message) {
        ProxyServer.getInstance().getScheduler().runAsync(Main.getInstance(), () -> {
            try (Jedis jedis = RedisService.newJedisInstance()) {
                authenticateJedis(jedis);
                jedis.publish(channel, message);
            }
        });
    }

    @Override
    public void HashSetValue(String key, String field, String value) {
        Main.getInstance().getProxy().getScheduler().runAsync(Main.getInstance(), () -> {
            try (Jedis jedis = RedisService.newJedisInstance()) {
                authenticateJedis(jedis);
                jedis.hset(key, field, value);
            }
        });
    }

    @Override
    public String HashGetValue(String targetKey, String field) {
        String key;
        try (Jedis jedis = RedisService.newJedisInstance()) {
            authenticateJedis(jedis);
            key = jedis.hget(targetKey, field);

        }
        return key;
    }

    @Override
    public Set<String> getArray(String key) {
        Set<String> set;
        try (Jedis jedis = RedisService.newJedisInstance()) {
            authenticateJedis(jedis);
            set = jedis.smembers(key);

        }
        return set;
    }

    @Override
    public void addToArray(String key, String value) {
        Main.getInstance().getProxy().getScheduler().runAsync(Main.getInstance(), () -> {
            try (Jedis jedis = RedisService.newJedisInstance()) {
                authenticateJedis(jedis);
                jedis.sadd(key, value);
            }
        });
    }

    @Override
    public void removeFromArray(String key, String value) {
        Main.getInstance().getProxy().getScheduler().runAsync(Main.getInstance(), () -> {
            try (Jedis jedis = RedisService.newJedisInstance()) {
                authenticateJedis(jedis);
                jedis.srem(key, value);
            }
        });
    }

    @Override
    public void removeKey(String key) {
        Main.getInstance().getProxy().getScheduler().runAsync(Main.getInstance(), () -> {
            try (Jedis jedis = RedisService.newJedisInstance()) {
                authenticateJedis(jedis);
                jedis.del(key);
            }
        });
    }

}