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

public final class SignCheck
extends Check {
    private final Map<UUID, AtomicInteger> signCounts = new ConcurrentHashMap<UUID, AtomicInteger>();
    private final Map<UUID, Long> lastReset = new ConcurrentHashMap<UUID, Long>();

    public SignCheck(RGuard plugin) {
        super(plugin, "Sign");
    }

    @Override
    public boolean process(Player player, Object packet) {
        int count;
        long last;
        if (!packet.getClass().getSimpleName().equals("PacketPlayInUpdateSign")) {
            return true;
        }
        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();
        if (now - (last = this.lastReset.computeIfAbsent(uuid, k -> now).longValue()) > 1000L) {
            this.signCounts.put(uuid, new AtomicInteger(0));
            this.lastReset.put(uuid, now);
        }
        if ((count = this.signCounts.computeIfAbsent(uuid, k -> new AtomicInteger(0)).incrementAndGet()) > this.plugin.getSettings().getSignMaxPacketsPerSecond()) {
            this.flag(player, "Sign Update Flood");
            return false;
        }
        Object linesObj = ReflectionUtil.getFieldValue(packet, "b");
        if (linesObj == null) {
            linesObj = ReflectionUtil.getFieldValue(packet, "lines");
        }
        if (linesObj instanceof Object[]) {
            Object[] lines;
            for (Object line : lines = (Object[])linesObj) {
                Object textObj;
                if (line == null) continue;
                String text = null;
                text = line instanceof String ? (String)line : ((textObj = ReflectionUtil.getFieldValue(line, "b")) instanceof String ? (String)textObj : line.toString());
                if (text == null) continue;
                if (text.length() > this.plugin.getSettings().getSignMaxLineLength()) {
                    this.flag(player, "Oversized Sign Line (" + text.length() + ")");
                    return false;
                }
                if (!text.contains("\"extra\"") && !text.contains("\"text\"") || this.getJsonNestingDepth(text) <= this.plugin.getSettings().getSignMaxJsonDepth()) continue;
                this.flag(player, "Sign Nested JSON Stack Bomb");
                return false;
            }
        }
        return true;
    }

    private int getJsonNestingDepth(String str) {
        int maxDepth = 0;
        int currentDepth = 0;
        for (int i = 0; i < str.length(); ++i) {
            char c = str.charAt(i);
            if (c == '{' || c == '[') {
                if (++currentDepth <= maxDepth) continue;
                maxDepth = currentDepth;
                continue;
            }
            if (c != '}' && c != ']' || currentDepth <= 0) continue;
            --currentDepth;
        }
        return maxDepth;
    }

    @Override
    public void remove(Player player) {
        this.signCounts.remove(player.getUniqueId());
        this.lastReset.remove(player.getUniqueId());
    }
}

