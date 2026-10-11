/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.Channel
 *  io.netty.channel.ChannelHandler
 *  io.netty.channel.ChannelPipeline
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 */
package dev.reatyzr.rguard.channel;

import dev.reatyzr.rguard.RGuard;
import dev.reatyzr.rguard.channel.PacketHandler;
import dev.reatyzr.rguard.util.ReflectionUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import java.util.NoSuchElementException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class PacketInjector {
    public static final String HANDLER_NAME = "rguard_packet_handler";
    private final RGuard plugin;

    public PacketInjector(RGuard plugin) {
        this.plugin = plugin;
    }

    public void inject(Player player) {
        Channel channel = ReflectionUtil.getChannel(player);
        if (channel == null) {
            return;
        }
        channel.eventLoop().execute(() -> {
            try {
                ChannelPipeline pipeline = channel.pipeline();
                if (pipeline.get(HANDLER_NAME) != null) {
                    pipeline.remove(HANDLER_NAME);
                }
                if (pipeline.get("packet_handler") != null) {
                    pipeline.addBefore("packet_handler", HANDLER_NAME, (ChannelHandler)new PacketHandler(this.plugin, player));
                } else {
                    pipeline.addFirst(HANDLER_NAME, (ChannelHandler)new PacketHandler(this.plugin, player));
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        });
    }

    public void uninject(Player player) {
        Channel channel = ReflectionUtil.getChannel(player);
        if (channel == null) {
            return;
        }
        channel.eventLoop().execute(() -> {
            try {
                ChannelPipeline pipeline = channel.pipeline();
                if (pipeline.get(HANDLER_NAME) != null) {
                    pipeline.remove(HANDLER_NAME);
                }
            }
            catch (NoSuchElementException noSuchElementException) {
            }
            catch (Exception exception) {
                // empty catch block
            }
        });
    }

    public void injectAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            this.inject(player);
        }
    }

    public void uninjectAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            this.uninject(player);
        }
    }
}

