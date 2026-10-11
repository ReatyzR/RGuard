/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package dev.reatyzr.rguard.guard;

import dev.reatyzr.rguard.RGuard;
import org.bukkit.entity.Player;

public abstract class Check {
    protected final RGuard plugin;
    private final String name;

    public Check(RGuard plugin, String name) {
        this.plugin = plugin;
        this.name = name;
    }

    public abstract boolean process(Player var1, Object var2);

    protected void flag(Player player, String exploitType) {
        int vl = this.plugin.getViolationManager().addViolation(player, exploitType);
        this.plugin.getAlertManager().sendAlert(player, exploitType, vl);
    }

    public String getName() {
        return this.name;
    }

    public void remove(Player player) {
    }
}

