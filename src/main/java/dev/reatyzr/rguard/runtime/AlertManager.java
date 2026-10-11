/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 */
package dev.reatyzr.rguard.runtime;

import dev.reatyzr.rguard.RGuard;
import dev.reatyzr.rguard.util.ColorUtil;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class AlertManager {
    private final RGuard plugin;
    private final Set<UUID> disabledAlertPlayers = new HashSet<UUID>();
    private final Map<String, Long> lastAlertTime = new ConcurrentHashMap<String, Long>();

    public AlertManager(RGuard plugin) {
        this.plugin = plugin;
    }

    public void sendAlert(Player player, String exploitType, int violationLevel) {
        String key = player.getUniqueId() + ":" + exploitType;
        long now = System.currentTimeMillis();
        Long lastTime = this.lastAlertTime.get(key);
        if (lastTime != null && now - lastTime < this.plugin.getSettings().getAlertCooldownMs()) {
            return;
        }
        this.lastAlertTime.put(key, now);
        String message = this.plugin.getSettings().getAlertMessage().replace("%player%", player.getName()).replace("%exploit_type%", exploitType).replace("%vl%", String.valueOf(violationLevel));
        String formatted = ColorUtil.color(message);
        if (this.plugin.getSettings().isLogToConsole()) {
            Bukkit.getConsoleSender().sendMessage(formatted);
        }
        if (this.plugin.getSettings().isBroadcastAlerts()) {
            Bukkit.broadcastMessage((String)formatted);
            return;
        }
        String perm = this.plugin.getSettings().getAlertPermission();
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (!online.hasPermission(perm) || this.disabledAlertPlayers.contains(online.getUniqueId())) continue;
            online.sendMessage(formatted);
        }
    }

    public boolean toggleAlerts(Player player) {
        UUID uuid = player.getUniqueId();
        if (this.disabledAlertPlayers.contains(uuid)) {
            this.disabledAlertPlayers.remove(uuid);
            return true;
        }
        this.disabledAlertPlayers.add(uuid);
        return false;
    }

    public boolean hasAlertsEnabled(Player player) {
        return !this.disabledAlertPlayers.contains(player.getUniqueId());
    }

    public void removePlayer(UUID uuid) {
        this.disabledAlertPlayers.remove(uuid);
    }
}

