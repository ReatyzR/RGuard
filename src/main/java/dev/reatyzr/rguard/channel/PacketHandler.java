/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.ChannelDuplexHandler
 *  io.netty.channel.ChannelHandlerContext
 *  org.bukkit.entity.Player
 */
package dev.reatyzr.rguard.channel;

import dev.reatyzr.rguard.RGuard;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import org.bukkit.entity.Player;

public final class PacketHandler
extends ChannelDuplexHandler {
    private final RGuard plugin;
    private final Player player;

    public PacketHandler(RGuard plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!this.player.isOnline()) {
            super.channelRead(ctx, msg);
            return;
        }
        try {
            if (!this.plugin.getCheckManager().processPacket(this.player, msg)) {
                return;
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        super.channelRead(ctx, msg);
    }
}

