/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 */
package dev.reatyzr.rguard.util;

import org.bukkit.ChatColor;

public final class ColorUtil {
    private ColorUtil() {
    }

    public static String color(String message) {
        if (message == null) {
            return "";
        }
        return ChatColor.translateAlternateColorCodes((char)'&', (String)message);
    }
}

