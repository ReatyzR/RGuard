/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerChangedWorldEvent
 *  org.bukkit.event.player.PlayerJoinEvent
 *  org.bukkit.event.player.PlayerQuitEvent
 *  org.bukkit.event.player.PlayerRespawnEvent
 *  org.bukkit.event.player.PlayerTeleportEvent
 */
package dev.reatyzr.rguard.listener;

import dev.reatyzr.rguard.RGuard;
import dev.reatyzr.rguard.guard.Check;
import dev.reatyzr.rguard.guard.impl.MovementCheck;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public final class PlayerListener
implements Listener {
    private final RGuard plugin;

    public PlayerListener(RGuard plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        this.plugin.getPacketInjector().inject(player);
        this.updateMovementGrace(player);
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        this.plugin.getPacketInjector().uninject(player);
        this.plugin.getViolationManager().remove(player.getUniqueId());
        this.plugin.getAlertManager().removePlayer(player.getUniqueId());
        this.plugin.getCheckManager().removePlayer(player);
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        for (Check check : this.plugin.getCheckManager().getChecks()) {
            if (!(check instanceof MovementCheck)) continue;
            ((MovementCheck)check).updateLocation(player.getUniqueId(), event.getTo());
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        for (Check check : this.plugin.getCheckManager().getChecks()) {
            if (!(check instanceof MovementCheck)) continue;
            ((MovementCheck)check).updateLocation(player.getUniqueId(), event.getRespawnLocation());
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onWorldChange(PlayerChangedWorldEvent event) {
        this.updateMovementGrace(event.getPlayer());
    }

    private void updateMovementGrace(Player player) {
        for (Check check : this.plugin.getCheckManager().getChecks()) {
            if (!(check instanceof MovementCheck)) continue;
            ((MovementCheck)check).setTeleportGrace(player.getUniqueId());
        }
    }
}

