/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  org.bukkit.entity.Player
 */
package dev.reatyzr.rguard.guard.impl;

import dev.reatyzr.rguard.RGuard;
import dev.reatyzr.rguard.guard.Check;
import dev.reatyzr.rguard.util.NbtSecurity;
import dev.reatyzr.rguard.util.ReflectionUtil;
import io.netty.buffer.ByteBuf;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.bukkit.entity.Player;

public final class PayloadCheck
extends Check {
    private final Map<UUID, AtomicInteger> payloadCount = new ConcurrentHashMap<UUID, AtomicInteger>();
    private final Map<UUID, Long> lastReset = new ConcurrentHashMap<UUID, Long>();

    public PayloadCheck(RGuard plugin) {
        super(plugin, "Payload");
    }

    @Override
    public boolean process(Player player, Object packet) {
        int count;
        long last;
        if (!packet.getClass().getSimpleName().equals("PacketPlayInCustomPayload")) {
            return true;
        }
        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();
        if (now - (last = this.lastReset.computeIfAbsent(uuid, k -> now).longValue()) > 1000L) {
            this.payloadCount.put(uuid, new AtomicInteger(0));
            this.lastReset.put(uuid, now);
        }
        if ((count = this.payloadCount.computeIfAbsent(uuid, k -> new AtomicInteger(0)).incrementAndGet()) > this.plugin.getSettings().getPayloadMaxFrequency()) {
            this.flag(player, "Payload Flood");
            return false;
        }
        String channel = (String)ReflectionUtil.getFieldValue(packet, "a");
        Object dataObj = ReflectionUtil.getFieldValue(packet, "b");
        if (channel == null || channel.isEmpty() || channel.length() > 64) {
            this.flag(player, "Invalid Channel");
            return false;
        }
        if (dataObj == null) {
            return true;
        }
        ByteBuf byteBuf = null;
        if (dataObj instanceof ByteBuf) {
            byteBuf = (ByteBuf)dataObj;
        } else {
            Object bufField = ReflectionUtil.getFieldValue(dataObj, "a");
            if (bufField instanceof ByteBuf) {
                byteBuf = (ByteBuf)bufField;
            }
        }
        if (byteBuf == null) {
            return true;
        }
        int readableBytes = byteBuf.readableBytes();
        if (readableBytes > this.plugin.getSettings().getPayloadMaxBytes()) {
            this.flag(player, "Oversized Payload (" + readableBytes + "B)");
            return false;
        }
        byte[] bytes = new byte[readableBytes];
        int readerIndex = byteBuf.readerIndex();
        byteBuf.getBytes(readerIndex, bytes);
        if (channel.equals("REGISTER") || channel.equals("UNREGISTER")) {
            if (readableBytes > this.plugin.getSettings().getPayloadMaxRegisterBytes()) {
                this.flag(player, "Register Payload Overflow");
                return false;
            }
            int nullCount = 0;
            for (byte b : bytes) {
                if (b != 0) continue;
                ++nullCount;
            }
            if (nullCount > this.plugin.getSettings().getPayloadMaxChannelsRegister()) {
                this.flag(player, "Channel Register Flood");
                return false;
            }
            return true;
        }
        if (channel.equalsIgnoreCase("HWID")) {
            if (readableBytes > 512) {
                this.flag(player, "HWID Payload Overflow");
                return false;
            }
            try {
                DataInputStream dis = new DataInputStream(new ByteArrayInputStream(bytes));
                byte[] nameBytes = new byte[Math.min(readableBytes, 64)];
                dis.read(nameBytes);
                if (dis.available() >= 8) {
                    int len1 = dis.readInt();
                    int len2 = dis.readInt();
                    if (len1 < 0 || len2 < 0 || len1 > 2048 || len2 > 2048 || len1 + len2 > readableBytes) {
                        this.flag(player, "HWID Memory Exhaustion");
                        return false;
                    }
                }
            }
            catch (Exception e) {
                this.flag(player, "Malformed HWID");
                return false;
            }
            return true;
        }
        if (channel.equalsIgnoreCase("Teyyap")) {
            if (readableBytes > 64) {
                this.flag(player, "Teyyap Channel Flood");
                return false;
            }
            return true;
        }
        if (channel.equals("MC|BEdit") || channel.equals("MC|BSign")) {
            if (readableBytes > this.plugin.getSettings().getNbtMaxTotalSizeBytes()) {
                this.flag(player, "Book NBT Bomb");
                return false;
            }
            try {
                Object tag;
                Object nmsItemStack = ReflectionUtil.getFieldValue(packet, "c");
                if (nmsItemStack != null && (tag = ReflectionUtil.getFieldValue(nmsItemStack, "tag")) != null) {
                    if (!NbtSecurity.validateNbtCompound(tag, this.plugin.getSettings())) {
                        this.flag(player, "Book Tag Flood");
                        return false;
                    }
                    Object pages = ReflectionUtil.getFieldValue(tag, "pages");
                    if (pages != null && !NbtSecurity.validateBookPages(pages, this.plugin.getSettings())) {
                        this.flag(player, "Book Page Overflow");
                        return false;
                    }
                }
            }
            catch (Exception nmsItemStack) {
                // empty catch block
            }
            return true;
        }
        if (channel.equals("customparticleeffects") || channel.equals("setEntityModelBipedRotation") || channel.equals("playerdab") || channel.equals("title") || channel.equals("hurtcamerashakemultiplier") || channel.equals("maxstacksize") || channel.equals("openenterpingui") || channel.equals("chunkborder") || channel.equals("setanglesandrecoil")) {
            String content = new String(bytes, StandardCharsets.UTF_8);
            if (content.length() > 512) {
                this.flag(player, "Custom Channel Payload Overflow");
                return false;
            }
            if ((content.contains("2147483647") || content.contains("99999") || content.contains("9999.9")) && (channel.equals("hurtcamerashakemultiplier") || channel.equals("maxstacksize") || channel.equals("setEntityModelBipedRotation"))) {
                this.flag(player, "Malformed Parameter Exploit");
                return false;
            }
        }
        return true;
    }

    @Override
    public void remove(Player player) {
        this.payloadCount.remove(player.getUniqueId());
        this.lastReset.remove(player.getUniqueId());
    }
}

