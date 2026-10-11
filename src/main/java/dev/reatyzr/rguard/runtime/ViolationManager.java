/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 */
package dev.reatyzr.rguard.runtime;

import dev.reatyzr.rguard.RGuard;
import dev.reatyzr.rguard.util.ColorUtil;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public final class ViolationManager {
    private final RGuard plugin;
    private final Map<UUID, AtomicInteger> violationCounts = new ConcurrentHashMap<UUID, AtomicInteger>();
    private final Map<UUID, Long> lastViolationTime = new ConcurrentHashMap<UUID, Long>();

    public ViolationManager(RGuard plugin) {
        this.plugin = plugin;
    }

    public int addViolation(Player player, String exploitType) {
        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();
        Long lastTime = this.lastViolationTime.get(uuid);
        long resetMillis = (long)this.plugin.getSettings().getViolationResetSeconds() * 1000L;
        if (lastTime != null && now - lastTime > resetMillis) {
            this.violationCounts.remove(uuid);
        }
        this.lastViolationTime.put(uuid, now);
        int currentVl = this.violationCounts.computeIfAbsent(uuid, k -> new AtomicInteger(0)).incrementAndGet();
        if (this.plugin.getSettings().isAutoKick() && currentVl >= this.plugin.getSettings().getMaxViolations()) {
            this.kickPlayer(player, exploitType);
        }
        return currentVl;
    }

    public int getViolations(UUID uuid) {
        AtomicInteger count = this.violationCounts.get(uuid);
        return count == null ? 0 : count.get();
    }

    public void remove(UUID uuid) {
        this.violationCounts.remove(uuid);
        this.lastViolationTime.remove(uuid);
    }

    public void clear() {
        this.violationCounts.clear();
        this.lastViolationTime.clear();
    }

    private void kickPlayer(Player player, String exploitType) {
        Bukkit.getScheduler().runTask((Plugin)this.plugin, () -> {
            if (player.isOnline()) {
                String kickMsg = this.plugin.getSettings().getKickMessage().replace("%player%", player.getName()).replace("%exploit_type%", exploitType);
                player.kickPlayer(ColorUtil.color(kickMsg));
            }
        });
    }
}

