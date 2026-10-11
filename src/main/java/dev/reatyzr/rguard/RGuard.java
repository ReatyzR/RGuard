/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.PluginCommand
 *  org.bukkit.command.TabCompleter
 *  org.bukkit.event.Listener
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 */
package dev.reatyzr.rguard;

import dev.reatyzr.rguard.guard.CheckManager;
import dev.reatyzr.rguard.command.RGuardCommand;
import dev.reatyzr.rguard.settings.Settings;
import dev.reatyzr.rguard.listener.PlayerListener;
import dev.reatyzr.rguard.runtime.AlertManager;
import dev.reatyzr.rguard.runtime.ViolationManager;
import dev.reatyzr.rguard.channel.PacketInjector;
import java.io.File;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class RGuard
extends JavaPlugin {
    private static RGuard instance;
    private Settings settings;
    private ViolationManager violationManager;
    private AlertManager alertManager;
    private CheckManager checkManager;
    private PacketInjector packetInjector;

    private static final String STARTUP_BANNER = "\n" +
            "_____   _____                      _ \n" +
            "|  __ \\ / ____|                    | |\n" +
            "| |__) | |  __ _   _ a r d  b y   | |\n" +
            "|  _  /| | |_ | | | | __ / _ \\ / _` |\n" +
            "| | \\ \\| |_*| | |*| |/ _/  _*/ (*| |\n" +
            "|*|  \\_\\\\_____|\\_*,*/__/ \\___|\\_*,_|\n" +
            "      [ RGuard 1.8.9 Runtime Shield / Protocol Guard ]\n" +
            "      [ Developed by ReatyzR ]";

    public void onEnable() {
        instance = this;
        getLogger().info(STARTUP_BANNER);
        this.saveDefaultConfig();
        this.ensureLocaleFiles();
        this.loadConfiguration();
        this.violationManager = new ViolationManager(this);
        this.alertManager = new AlertManager(this);
        this.checkManager = new CheckManager(this);
        this.packetInjector = new PacketInjector(this);
        Bukkit.getPluginManager().registerEvents((Listener)new PlayerListener(this), (Plugin)this);
        RGuardCommand cmd = new RGuardCommand(this);
        PluginCommand command = this.getCommand("rguard");
        if (command != null) {
            command.setExecutor((CommandExecutor)cmd);
            command.setTabCompleter((TabCompleter)cmd);
        }
        this.packetInjector.injectAll();
    }

    public void onDisable() {
        if (this.packetInjector != null) {
            this.packetInjector.uninjectAll();
        }
        if (this.violationManager != null) {
            this.violationManager.clear();
        }
        instance = null;
    }

    public void reloadConfiguration() {
        this.reloadConfig();
        this.loadConfiguration();
    }

    private void ensureLocaleFiles() {
        if (!new File(this.getDataFolder(), "tr.yml").exists()) this.saveResource("tr.yml", false);
        if (!new File(this.getDataFolder(), "eng.yml").exists()) this.saveResource("eng.yml", false);
    }
    private void loadConfiguration() {
        String language = this.getConfig().getString("language", "tr").toLowerCase();
        if (!language.equals("eng")) language = "tr";
        FileConfiguration locale = YamlConfiguration.loadConfiguration(new File(this.getDataFolder(), language + ".yml"));
        this.settings = new Settings(this.getConfig(), locale, language);
    }

    public static RGuard getInstance() {
        return instance;
    }

    public Settings getSettings() {
        return this.settings;
    }

    public ViolationManager getViolationManager() {
        return this.violationManager;
    }

    public AlertManager getAlertManager() {
        return this.alertManager;
    }

    public CheckManager getCheckManager() {
        return this.checkManager;
    }

    public PacketInjector getPacketInjector() {
        return this.packetInjector;
    }
}

