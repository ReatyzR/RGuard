/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
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
import org.bukkit.Location;
import org.bukkit.entity.Player;

public final class MovementCheck
extends Check {
    private final Map<UUID, Location> lastLocations = new ConcurrentHashMap<UUID, Location>();
    private final Map<UUID, Long> teleportGrace = new ConcurrentHashMap<UUID, Long>();
    private final Map<UUID, AtomicInteger> packetCounts = new ConcurrentHashMap<UUID, AtomicInteger>();
    private final Map<UUID, Long> lastReset = new ConcurrentHashMap<UUID, Long>();

    public MovementCheck(RGuard plugin) {
        super(plugin, "Movement");
    }

    @Override
    public boolean process(Player player, Object packet) {
        int count;
        long last;
        String packetName = packet.getClass().getSimpleName();
        if (!packetName.startsWith("PacketPlayInFlying")) {
            return true;
        }
        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();
        if (now - (last = this.lastReset.computeIfAbsent(uuid, k -> now).longValue()) > 1000L) {
            this.packetCounts.put(uuid, new AtomicInteger(0));
            this.lastReset.put(uuid, now);
        }
        if ((count = this.packetCounts.computeIfAbsent(uuid, k -> new AtomicInteger(0)).incrementAndGet()) > this.plugin.getSettings().getMovementMaxPacketsPerSecond()) {
            this.flag(player, "Movement Packet Flood");
            return false;
        }
        Boolean hasPos = (Boolean)ReflectionUtil.getFieldValue(packet, "hasPos");
        Boolean hasLook = (Boolean)ReflectionUtil.getFieldValue(packet, "hasLook");
        if (hasPos == null) {
            hasPos = packetName.contains("Position");
        }
        if (hasLook == null) {
            hasLook = packetName.contains("Look");
        }
        if (hasLook.booleanValue()) {
            Float yaw = (Float)ReflectionUtil.getFieldValue(packet, "yaw");
            Float pitch = (Float)ReflectionUtil.getFieldValue(packet, "pitch");
            if (yaw != null && (Float.isNaN(yaw.floatValue()) || Float.isInfinite(yaw.floatValue()))) {
                this.flag(player, "Invalid Yaw NaN");
                return false;
            }
            if (pitch != null) {
                if (Float.isNaN(pitch.floatValue()) || Float.isInfinite(pitch.floatValue())) {
                    this.flag(player, "Invalid Pitch NaN");
                    return false;
                }
                if ((double)pitch.floatValue() > this.plugin.getSettings().getMovementMaxPitch() || (double)pitch.floatValue() < this.plugin.getSettings().getMovementMinPitch()) {
                    this.flag(player, "Extreme Pitch (" + pitch + ")");
                    return false;
                }
            }
        }
        if (hasPos.booleanValue()) {
            Double x = (Double)ReflectionUtil.getFieldValue(packet, "x");
            Double y = (Double)ReflectionUtil.getFieldValue(packet, "y");
            Double z = (Double)ReflectionUtil.getFieldValue(packet, "z");
            if (x == null || y == null || z == null) {
                return true;
            }
            if (Double.isNaN(x) || Double.isNaN(y) || Double.isNaN(z) || Double.isInfinite(x) || Double.isInfinite(y) || Double.isInfinite(z)) {
                this.flag(player, "Position NaN/Infinity");
                return false;
            }
            double maxCoord = this.plugin.getSettings().getMovementMaxCoordinate();
            if (Math.abs(x) > maxCoord || Math.abs(z) > maxCoord || Math.abs(y) > 4000.0) {
                this.flag(player, "Out of Bounds Position");
                return false;
            }
            Long grace = this.teleportGrace.get(uuid);
            boolean inGrace = grace != null && now - grace < 2000L;
            Location lastLoc = this.lastLocations.get(uuid);
            if (lastLoc != null && !inGrace) {
                double deltaX = Math.abs(x - lastLoc.getX());
                double deltaZ = Math.abs(z - lastLoc.getZ());
                if ((deltaX > 250.0 || deltaZ > 250.0) && count > 15) {
                    this.flag(player, "Chunk Load Bomb");
                    return false;
                }
            }
            this.lastLocations.put(uuid, new Location(player.getWorld(), x.doubleValue(), y.doubleValue(), z.doubleValue()));
        }
        return true;
    }

    public void setTeleportGrace(UUID uuid) {
        this.teleportGrace.put(uuid, System.currentTimeMillis());
    }

    public void updateLocation(UUID uuid, Location location) {
        this.lastLocations.put(uuid, location);
        this.teleportGrace.put(uuid, System.currentTimeMillis());
    }

    @Override
    public void remove(Player player) {
        this.lastLocations.remove(player.getUniqueId());
        this.teleportGrace.remove(player.getUniqueId());
        this.packetCounts.remove(player.getUniqueId());
        this.lastReset.remove(player.getUniqueId());
    }
}

