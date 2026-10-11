/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package dev.reatyzr.rguard.guard;

import dev.reatyzr.rguard.RGuard;
import dev.reatyzr.rguard.guard.Check;
import dev.reatyzr.rguard.guard.impl.EntityCheck;
import dev.reatyzr.rguard.guard.impl.MovementCheck;
import dev.reatyzr.rguard.guard.impl.NbtCheck;
import dev.reatyzr.rguard.guard.impl.PacketRateCheck;
import dev.reatyzr.rguard.guard.impl.PayloadCheck;
import dev.reatyzr.rguard.guard.impl.SignCheck;
import dev.reatyzr.rguard.guard.impl.WindowCheck;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.entity.Player;

public final class CheckManager {
    private final List<Check> checks = new ArrayList<Check>();

    public CheckManager(RGuard plugin) {
        this.checks.add(new PayloadCheck(plugin));
        this.checks.add(new NbtCheck(plugin));
        this.checks.add(new MovementCheck(plugin));
        this.checks.add(new WindowCheck(plugin));
        this.checks.add(new SignCheck(plugin));
        this.checks.add(new PacketRateCheck(plugin));
        this.checks.add(new EntityCheck(plugin));
    }

    public boolean processPacket(Player player, Object packet) {
        if (player == null || packet == null) {
            return true;
        }
        for (Check check : this.checks) {
            try {
                if (check.process(player, packet)) continue;
                return false;
            }
            catch (Exception exception) {
            }
        }
        return true;
    }

    public void removePlayer(Player player) {
        for (Check check : this.checks) {
            check.remove(player);
        }
    }

    public List<Check> getChecks() {
        return Collections.unmodifiableList(this.checks);
    }
}

