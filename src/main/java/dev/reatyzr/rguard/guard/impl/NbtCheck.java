/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package dev.reatyzr.rguard.guard.impl;

import dev.reatyzr.rguard.RGuard;
import dev.reatyzr.rguard.guard.Check;
import dev.reatyzr.rguard.util.NbtSecurity;
import dev.reatyzr.rguard.util.ReflectionUtil;
import org.bukkit.entity.Player;

public final class NbtCheck
extends Check {
    public NbtCheck(RGuard plugin) {
        super(plugin, "NBT");
    }

    @Override
    public boolean process(Player player, Object packet) {
        String packetName = packet.getClass().getSimpleName();
        if (packetName.equals("PacketPlayInSetCreativeSlot")) {
            Object itemStack = ReflectionUtil.getFieldValue(packet, "b");
            if (itemStack != null) {
                return this.validateItem(player, itemStack, "Creative NBT Bomb");
            }
        } else if (packetName.equals("PacketPlayInBlockPlace")) {
            Object itemStack = ReflectionUtil.getFieldValue(packet, "d");
            if (itemStack == null) {
                itemStack = ReflectionUtil.getFieldValue(packet, "itemStack");
            }
            if (itemStack != null) {
                return this.validateItem(player, itemStack, "Placement NBT Bomb");
            }
        }
        return true;
    }

    private boolean validateItem(Player player, Object nmsItemStack, String exploitName) {
        Object tagCompound = ReflectionUtil.getFieldValue(nmsItemStack, "tag");
        if (tagCompound == null) {
            return true;
        }
        if (!NbtSecurity.validateNbtCompound(tagCompound, this.plugin.getSettings())) {
            this.flag(player, exploitName);
            return false;
        }
        Object pages = ReflectionUtil.getFieldValue(tagCompound, "pages");
        if (pages != null && !NbtSecurity.validateBookPages(pages, this.plugin.getSettings())) {
            this.flag(player, "Book Page Exploit");
            return false;
        }
        return true;
    }
}

