package me.krutikov.redisconnector.redis.utils;

import lombok.experimental.UtilityClass;
import me.krutikov.redisconnector.Main;

@UtilityClass
public class RedirectUtils {
    public void connectPlayer(String playerName, String targetServer) {
        Main.getRedisService().pub("redirect-manager", playerName + " " + targetServer);
    }
}
