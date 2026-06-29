package com.sunshulkers.managers;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Менеджер для управления кулдаунами сообщений
 * Предотвращает спам одинаковых сообщений
 */
public class MessageCooldownManager implements Listener {

    // Хранит время последнего показа каждого типа сообщения для каждого игрока
    private final Map<String, Map<UUID, Long>> messageCooldowns = new HashMap<>();

    /**
     * Проверяет, можно ли показать сообщение игроку
     * @param player Игрок
     * @param messageType Тип сообщения
     * @param cooldownSeconds Кулдаун в секундах
     * @return true если можно показать сообщение
     */
    public boolean canShowMessage(Player player, String messageType, int cooldownSeconds) {
        Map<UUID, Long> playerCooldowns = messageCooldowns.computeIfAbsent(messageType, k -> new HashMap<>());

        UUID playerId = player.getUniqueId();
        Long lastShown = playerCooldowns.get(playerId);

        long currentTime = System.currentTimeMillis();

        if (lastShown == null || currentTime - lastShown >= cooldownSeconds * 1000L) {
            playerCooldowns.put(playerId, currentTime);
            return true;
        }

        return false;
    }

    /**
     * Очищает все кулдауны для игрока
     * @param player Игрок
     */
    public void clearPlayerCooldowns(Player player) {
        UUID playerId = player.getUniqueId();
        for (Map<UUID, Long> cooldowns : messageCooldowns.values()) {
            cooldowns.remove(playerId);
        }
    }

    /**
     * Очищает все кулдауны
     */
    public void clearAllCooldowns() {
        messageCooldowns.clear();
    }

    // Чистим кэш при выходе игрока, иначе мапа будет пухнуть до рестарта
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        clearPlayerCooldowns(event.getPlayer());
    }
}