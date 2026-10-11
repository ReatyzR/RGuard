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

public final class WindowCheck
extends Check {
    private final Map<UUID, AtomicInteger> clickCounts = new ConcurrentHashMap<UUID, AtomicInteger>();
    private final Map<UUID, AtomicInteger> transactionCounts = new ConcurrentHashMap<UUID, AtomicInteger>();
    private final Map<UUID, AtomicInteger> closeCounts = new ConcurrentHashMap<UUID, AtomicInteger>();
    private final Map<UUID, Long> lastReset = new ConcurrentHashMap<UUID, Long>();

    public WindowCheck(RGuard plugin) {
        super(plugin, "Window");
    }

    @Override
    public boolean process(Player player, Object packet) {
        long last;
        String packetName = packet.getClass().getSimpleName();
        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();
        if (now - (last = this.lastReset.computeIfAbsent(uuid, k -> now).longValue()) > 1000L) {
            this.clickCounts.put(uuid, new AtomicInteger(0));
            this.transactionCounts.put(uuid, new AtomicInteger(0));
            this.closeCounts.put(uuid, new AtomicInteger(0));
            this.lastReset.put(uuid, now);
        }
        if (packetName.equals("PacketPlayInCloseWindow")) {
            int count = this.closeCounts.computeIfAbsent(uuid, k -> new AtomicInteger(0)).incrementAndGet();
            if (count > this.plugin.getSettings().getWindowMaxClosePerSecond()) {
                this.flag(player, "Window Close Flood");
                return false;
            }
            Integer windowId = (Integer)ReflectionUtil.getFieldValue(packet, "id");
            if (windowId != null && (windowId < 0 || windowId > this.plugin.getSettings().getWindowMaxWindowId())) {
                this.flag(player, "Invalid Window ID (" + windowId + ")");
                return false;
            }
            return true;
        }
        if (packetName.equals("PacketPlayInWindowClick")) {
            int count = this.clickCounts.computeIfAbsent(uuid, k -> new AtomicInteger(0)).incrementAndGet();
            if (count > this.plugin.getSettings().getWindowMaxClicksPerSecond()) {
                this.flag(player, "Window Click Flood");
                return false;
            }
            Integer slot = (Integer)ReflectionUtil.getFieldValue(packet, "slot");
            Integer windowId = (Integer)ReflectionUtil.getFieldValue(packet, "a");
            if (windowId == null) {
                windowId = (Integer)ReflectionUtil.getFieldValue(packet, "id");
            }
            if (windowId != null && (windowId < 0 || windowId > this.plugin.getSettings().getWindowMaxWindowId())) {
                this.flag(player, "Window Click Invalid ID");
                return false;
            }
            if (slot != null && slot != -999 && (slot < 0 || slot > this.plugin.getSettings().getWindowMaxSlotId())) {
                this.flag(player, "Invalid Slot Click (" + slot + ")");
                return false;
            }
            return true;
        }
        if (packetName.equals("PacketPlayInTransaction")) {
            int count = this.transactionCounts.computeIfAbsent(uuid, k -> new AtomicInteger(0)).incrementAndGet();
            if (count > this.plugin.getSettings().getWindowMaxTransactionsPerSecond()) {
                this.flag(player, "Transaction Flood / Desync");
                return false;
            }
            Integer windowId = (Integer)ReflectionUtil.getFieldValue(packet, "a");
            if (windowId != null && (windowId < 0 || windowId > this.plugin.getSettings().getWindowMaxWindowId())) {
                this.flag(player, "Invalid Transaction Window");
                return false;
            }
            return true;
        }
        return true;
    }

    @Override
    public void remove(Player player) {
        this.clickCounts.remove(player.getUniqueId());
        this.transactionCounts.remove(player.getUniqueId());
        this.closeCounts.remove(player.getUniqueId());
        this.lastReset.remove(player.getUniqueId());
    }
}

