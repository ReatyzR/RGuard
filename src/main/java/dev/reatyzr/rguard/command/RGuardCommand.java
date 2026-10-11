/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.command.TabCompleter
 *  org.bukkit.entity.Player
 */
package dev.reatyzr.rguard.command;

import dev.reatyzr.rguard.RGuard;
import dev.reatyzr.rguard.util.ColorUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public final class RGuardCommand
implements CommandExecutor,
TabCompleter {
    private final RGuard plugin;

    public RGuardCommand(RGuard plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String sub;
        if (!sender.hasPermission("rguard.admin")) {
            sender.sendMessage(ColorUtil.color(this.plugin.getSettings().getNoPermissionMessage()));
            return true;
        }
        if (args.length == 0) {
            this.sendHelp(sender);
            return true;
        }
        switch (sub = args[0].toLowerCase()) {
            case "reload": 
            case "rl": {
                this.plugin.reloadConfiguration();
                sender.sendMessage(ColorUtil.color(this.plugin.getSettings().getPrefix() + " " + this.plugin.getSettings().getReloadMessage()));
                break;
            }
            case "alerts": 
            case "notify": {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ColorUtil.color(this.plugin.getSettings().getPrefix() + " " + this.plugin.getSettings().getPlayerOnlyMessage()));
                    return true;
                }
                Player player = (Player)sender;
                boolean enabled = this.plugin.getAlertManager().toggleAlerts(player);
                if (enabled) {
                    player.sendMessage(ColorUtil.color(this.plugin.getSettings().getPrefix() + " " + this.plugin.getSettings().getAlertsEnabledMessage()));
                    break;
                }
                player.sendMessage(ColorUtil.color(this.plugin.getSettings().getPrefix() + " " + this.plugin.getSettings().getAlertsDisabledMessage()));
                break;
            }
            case "stats": 
            case "info": {
                sender.sendMessage(ColorUtil.color(this.plugin.getSettings().getStatsHeader()));
                sender.sendMessage(ColorUtil.color(this.plugin.getSettings().getStatsChecks() + this.plugin.getCheckManager().getChecks().size()));
                sender.sendMessage(ColorUtil.color(this.plugin.getSettings().getStatsPlayers() + Bukkit.getOnlinePlayers().size()));
                sender.sendMessage(ColorUtil.color(this.plugin.getSettings().getStatsRuntime() + Bukkit.getVersion()));
                break;
            }
            default: {
                this.sendHelp(sender);
            }
        }
        return true;
    }

    private void sendHelp(CommandSender sender) {
        for (String line : this.plugin.getSettings().getHelpMessage()) {
            sender.sendMessage(ColorUtil.color(line));
        }
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("rguard.admin")) {
            return Collections.emptyList();
        }
        if (args.length == 1) {
            List<String> list = Arrays.asList("reload", "alerts", "stats", "help");
            ArrayList<String> results = new ArrayList<String>();
            for (String s : list) {
                if (!s.toLowerCase().startsWith(args[0].toLowerCase())) continue;
                results.add(s);
            }
            return results;
        }
        return Collections.emptyList();
    }
}

