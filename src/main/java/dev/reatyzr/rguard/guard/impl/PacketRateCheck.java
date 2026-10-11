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

public final class PacketRateCheck
extends Check {
    private final Map<UUID, Map<String, AtomicInteger>> packetRates = new ConcurrentHashMap<UUID, Map<String, AtomicInteger>>();
    private final Map<UUID, AtomicInteger> globalPacketCounts = new ConcurrentHashMap<UUID, AtomicInteger>();
    private final Map<UUID, Long> lastReset = new ConcurrentHashMap<UUID, Long>();

    public PacketRateCheck(RGuard plugin) {
        super(plugin, "PacketRate");
    }

    @Override
    public boolean process(Player player, Object packet) {
        int globalCount;
        long last;
        String packetName = packet.getClass().getSimpleName();
        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();
        if (now - (last = this.lastReset.computeIfAbsent(uuid, k -> now).longValue()) > 1000L) {
            this.packetRates.put(uuid, new ConcurrentHashMap());
            this.globalPacketCounts.put(uuid, new AtomicInteger(0));
            this.lastReset.put(uuid, now);
        }
        if ((globalCount = this.globalPacketCounts.computeIfAbsent(uuid, k -> new AtomicInteger(0)).incrementAndGet()) > this.plugin.getSettings().getGlobalMaxPacketsPerSecond()) {
            this.flag(player, "Global Packet Flood");
            return false;
        }
        Map rates = this.packetRates.computeIfAbsent(uuid, k -> new ConcurrentHashMap());
        AtomicInteger rateCounter = (AtomicInteger) rates.computeIfAbsent(packetName, k -> new AtomicInteger(0));
        int typeCount = rateCounter.incrementAndGet();
        switch (packetName) {
            case "PacketPlayInKeepAlive": {
                if (typeCount <= this.plugin.getSettings().getRateKeepAlive()) break;
                this.flag(player, "KeepAlive Flood");
                return false;
            }
            case "PacketPlayInClientStatus": {
                if (typeCount <= this.plugin.getSettings().getRateClientStatus()) break;
                this.flag(player, "ClientStatus Flood");
                return false;
            }
            case "PacketPlayInBlockPlace": {
                if (typeCount > this.plugin.getSettings().getRateBlockPlace()) {
                    this.flag(player, "BlockPlace Flood");
                    return false;
                }
                Object posObj = ReflectionUtil.getFieldValue(packet, "a");
                if (posObj == null) break;
                Integer x = (Integer)ReflectionUtil.getFieldValue(posObj, "a");
                Integer y = (Integer)ReflectionUtil.getFieldValue(posObj, "c");
                Integer z = (Integer)ReflectionUtil.getFieldValue(posObj, "d");
                if (x == null || y == null || z == null || x == -1 && y == -1 && z == -1) break;
                if (y < -64 || y > 320) {
                    this.flag(player, "Invalid BlockPlace Y");
                    return false;
                }
                if (Math.abs(x) <= 30000000 && Math.abs(z) <= 30000000) break;
                this.flag(player, "Invalid BlockPlace Coords");
                return false;
            }
            case "PacketPlayInBlockDig": {
                if (typeCount <= this.plugin.getSettings().getRateBlockDig()) break;
                this.flag(player, "BlockDig Flood");
                return false;
            }
            case "PacketPlayInSteerVehicle": {
                if (typeCount <= this.plugin.getSettings().getRateVehicleInput()) break;
                this.flag(player, "Vehicle Steer Flood");
                return false;
            }
            case "PacketPlayInSettings": {
                if (typeCount > this.plugin.getSettings().getRateClientSettings()) {
                    this.flag(player, "Settings Flood");
                    return false;
                }
                String locale = (String)ReflectionUtil.getFieldValue(packet, "a");
                if (locale == null || locale.length() <= 16) break;
                this.flag(player, "Invalid Locale Length");
                return false;
            }
            case "PacketPlayInResourcePackStatus": {
                if (typeCount <= this.plugin.getSettings().getRateResourcePack()) break;
                this.flag(player, "ResourcePack Status Flood");
                return false;
            }
            case "PacketPlayInChat": {
                if (typeCount > this.plugin.getSettings().getRateChat()) {
                    this.flag(player, "Chat Thread Flood");
                    return false;
                }
                String message = (String)ReflectionUtil.getFieldValue(packet, "a");
                if (message == null || message.length() <= 256) break;
                this.flag(player, "Oversized Chat Message");
                return false;
            }
        }
        return true;
    }

    @Override
    public void remove(Player player) {
        this.packetRates.remove(player.getUniqueId());
        this.globalPacketCounts.remove(player.getUniqueId());
        this.lastReset.remove(player.getUniqueId());
    }
}

