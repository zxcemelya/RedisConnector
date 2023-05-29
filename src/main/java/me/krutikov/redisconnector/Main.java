package me.krutikov.redisconnector;

import lombok.Getter;
import me.krutikov.redisconnector.redis.IRedis;
import me.krutikov.redisconnector.redis.impl.RedisService;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.net.InetSocketAddress;

public final class Main extends Plugin {

    @Getter
    private static Main instance;

    @Getter
    private static IRedis redisService;


    @Override
    public void onEnable() {
        redisService = new RedisService();
        instance = this;
        jedisPubSub();
        redisService.pub("register-service", "autoreg-all");
        // Plugin startup logic
    }

    void jedisPubSub() {
        Runnable runnable = () -> {
            JedisPubSub jedisPubSub = new JedisPubSub() {
                @Override
                public void onMessage(String channel, String message) {
                    switch (channel) {
                        case ("proxy-message"): {
                            System.out.println(message);
                            for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                                player.sendMessage(message);
                            }
                            break;
                        }
                        case ("redirect-manager"): {
                            String[] parts = message.split(" ");
                            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(parts[0]);
                            ServerInfo server = ProxyServer.getInstance().getServerInfo(parts[1]);
                            if (player == null) return;
                            if (server == null) {
                                player.sendMessage("§cОшибка подключения к серверу " + parts[1]);
                                return;
                            }
                            if (player.getServer().getInfo().getName().equalsIgnoreCase(server.getName())) {
                                player.sendMessage("§cВы уже подключены к этому серверу!");
                                return;
                            }
                            player.connect(server);

                            player.sendMessage("§aПодключаем вас к серверу " + server.getName());
                            break;
                        }
                        case ("register-service"): {
                            if (message.equals("autoreg-all")) return;
                            String[] parts = message.split(" ");
                            String serverName = parts[0];
                            int port = Integer.parseInt(parts[1]);
                            ServerInfo serverInfo = ProxyServer.getInstance().constructServerInfo(serverName, new InetSocketAddress("localhost", port), "Server " + serverName, false);
                            ProxyServer.getInstance().getServers().put(serverName, serverInfo);
                            System.out.println("[Регистрация серверов] Успешно зарегистрирован сервер: " + serverName + " с портом " + port);
                            break;
                        }
                    }
                }
            };
            try (Jedis jedis = RedisService.newJedisInstance()) {
                redisService.authenticateJedis(jedis);
                jedis.subscribe(jedisPubSub, "proxy-message", "redirect-manager", "register-service");
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }
    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
