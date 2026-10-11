/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package dev.reatyzr.rguard.guard.impl;

import dev.reatyzr.rguard.RGuard;
import dev.reatyzr.rguard.guard.Check;
import dev.reatyzr.rguard.util.ReflectionUtil;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.bukkit.entity.Player;

public final class EntityCheck
extends Check {
    private final Map<UUID, AtomicInteger> useEntityCounts = new ConcurrentHashMap<UUID, AtomicInteger>();
    private final Map<UUID, AtomicInteger> entityActionCounts = new ConcurrentHashMap<UUID, AtomicInteger>();
    private final Map<UUID, Long> lastReset = new ConcurrentHashMap<UUID, Long>();

    public EntityCheck(RGuard plugin) {
        super(plugin, "Entity");
    }

    @Override
    public boolean process(Player player, Object packet) {
        long last;
        String packetName = packet.getClass().getSimpleName();
        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();
        if (now - (last = this.lastReset.computeIfAbsent(uuid, k -> now).longValue()) > 1000L) {
            this.useEntityCounts.put(uuid, new AtomicInteger(0));
            this.entityActionCounts.put(uuid, new AtomicInteger(0));
            this.lastReset.put(uuid, now);
        }
        if (packetName.equals("PacketPlayInEntityAction")) {
            int count = this.entityActionCounts.computeIfAbsent(uuid, k -> new AtomicInteger(0)).incrementAndGet();
            if (count > this.plugin.getSettings().getRateEntityAction()) {
                this.flag(player, "Entity Action Spam");
                return false;
            }
            return true;
        }
        if (packetName.equals("PacketPlayInUseEntity")) {
            int count = this.useEntityCounts.computeIfAbsent(uuid, k -> new AtomicInteger(0)).incrementAndGet();
            if (count > this.plugin.getSettings().getRateUseEntity()) {
                this.flag(player, "Entity Tracker Flood");
                return false;
            }
            Integer targetEntityId = (Integer)ReflectionUtil.getFieldValue(packet, "a");
            if (targetEntityId != null) {
                if (targetEntityId.intValue() == player.getEntityId()) {
                    this.flag(player, "Self Entity Interaction");
                    return false;
                }
                if (targetEntityId < 0) {
                    this.flag(player, "Invalid Entity Target");
                    return false;
                }
            }
            return true;
        }
        return true;
    }

    @Override
    public void remove(Player player) {
        this.useEntityCounts.remove(player.getUniqueId());
        this.entityActionCounts.remove(player.getUniqueId());
        this.lastReset.remove(player.getUniqueId());
    }
}

