/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.configuration.file.FileConfiguration
 */
package dev.reatyzr.rguard.settings;

import java.util.Arrays;
import java.util.List;
import org.bukkit.configuration.file.FileConfiguration;

public final class Settings {
    private final String prefix;
    private final String alertMessage;
    private final String kickMessage;
    private final String noPermissionMessage;
    private final String reloadMessage;
    private final List<String> helpMessage;
    private final String alertsEnabledMessage;
    private final String alertsDisabledMessage;
    private final String playerOnlyMessage;
    private final String statsHeader;
    private final String statsChecks;
    private final String statsPlayers;
    private final String statsRuntime;
    private final boolean broadcastAlerts;
    private final String alertPermission;
    private final boolean logToConsole;
    private final long alertCooldownMs;
    private final boolean autoKick;
    private final int maxViolations;
    private final int violationResetSeconds;
    private final int globalMaxPacketsPerSecond;
    private final int payloadMaxBytes;
    private final int payloadMaxChannelsRegister;
    private final int payloadMaxRegisterBytes;
    private final int payloadMaxFrequency;
    private final int nbtMaxDepth;
    private final int nbtMaxTags;
    private final int nbtMaxListElements;
    private final int nbtMaxStringLength;
    private final int nbtMaxBookPages;
    private final int nbtMaxBookPageLength;
    private final int nbtMaxTotalSizeBytes;
    private final int signMaxLineLength;
    private final int signMaxJsonDepth;
    private final int signMaxPacketsPerSecond;
    private final double movementMaxPitch;
    private final double movementMinPitch;
    private final double movementMaxCoordinate;
    private final double movementMaxDeltaDistance;
    private final int movementMaxPacketsPerSecond;
    private final int windowMaxWindowId;
    private final int windowMaxSlotId;
    private final int windowMaxClicksPerSecond;
    private final int windowMaxTransactionsPerSecond;
    private final int windowMaxClosePerSecond;
    private final int rateKeepAlive;
    private final int rateClientStatus;
    private final int rateBlockPlace;
    private final int rateBlockDig;
    private final int rateEntityAction;
    private final int rateUseEntity;
    private final int rateVehicleInput;
    private final int rateClientSettings;
    private final int rateResourcePack;
    private final int rateChat;

    public Settings(FileConfiguration config, FileConfiguration locale, String language) {
        this.prefix = config.getString("prefix", "&b&lRGuard &8»");
        this.alertMessage = locale.getString("messages.alert", "&b&lRGuard &8» &f%player% &7için şüpheli &d%exploit_type% &7trafiği durduruldu. &8[&dSeviye %vl%&8]");
        this.kickMessage = locale.getString("messages.kick", "&b&lRGuard\n\n&cGüvenlik doğrulaması başarısız oldu.\n&7Tespit: &f%exploit_type%");
        this.noPermissionMessage = locale.getString("messages.no-permission", "&cBu RGuard işlemi için gerekli yetkiniz bulunmuyor.");
        this.reloadMessage = locale.getString("messages.reload", "&aRGuard ayarları yenilendi.");
        this.helpMessage = language.equals("eng")
                ? Arrays.asList(
                    "&8&m----------------------------------------",
                    "&b&lRGuard &7| &fControl menu",
                    "&f/rguard reload &8- &7Reloads the protection profile",
                    "&f/rguard alerts &8- &7Toggles personal security alerts",
                    "&f/rguard stats &8- &7Shows the current protection status",
                    "&8&m----------------------------------------")
                : Arrays.asList(
                    "&8&m----------------------------------------",
                    "&b&lRGuard &7| &fYönetim menüsü",
                    "&f/rguard reload &8- &7Güvenlik profilini yeniler",
                    "&f/rguard alerts &8- &7Kişisel uyarıları açar veya kapatır",
                    "&f/rguard stats &8- &7Koruma durumunu görüntüler",
                    "&8&m----------------------------------------");
        this.alertsEnabledMessage = locale.getString("messages.alerts-enabled", "&aKişisel uyarılar etkinleştirildi.");
        this.alertsDisabledMessage = locale.getString("messages.alerts-disabled", "&cKişisel uyarılar devre dışı bırakıldı.");
        this.playerOnlyMessage = locale.getString("messages.player-only", "&cBu işlem yalnızca oyuncular tarafından kullanılabilir.");
        this.statsHeader = locale.getString("messages.stats.header", "&b&lRGuard &8» &fKoruma durumu");
        this.statsChecks = locale.getString("messages.stats.checks", "&7Aktif güvenlik denetimleri: &f");
        this.statsPlayers = locale.getString("messages.stats.players", "&7İzlenen oyuncular: &f");
        this.statsRuntime = locale.getString("messages.stats.runtime", "&7Çalışma ortamı: &f");
        this.broadcastAlerts = config.getBoolean("settings.broadcast-alerts", false);
        this.alertPermission = config.getString("settings.alert-permission", "rguard.alerts");
        this.logToConsole = config.getBoolean("settings.log-to-console", true);
        this.alertCooldownMs = config.getLong("settings.alert-cooldown-ms", 400L);
        this.autoKick = config.getBoolean("punishment.auto-kick", true);
        this.maxViolations = config.getInt("punishment.max-violations", 8);
        this.violationResetSeconds = config.getInt("punishment.violation-reset-seconds", 15);
        this.globalMaxPacketsPerSecond = config.getInt("limits.global.max-packets-per-second", 350);
        this.payloadMaxBytes = config.getInt("limits.payload.max-bytes", 16384);
        this.payloadMaxChannelsRegister = config.getInt("limits.payload.max-channels-register", 8);
        this.payloadMaxRegisterBytes = config.getInt("limits.payload.max-register-bytes", 512);
        this.payloadMaxFrequency = config.getInt("limits.payload.max-frequency", 25);
        this.nbtMaxDepth = config.getInt("limits.nbt.max-depth", 6);
        this.nbtMaxTags = config.getInt("limits.nbt.max-tags", 250);
        this.nbtMaxListElements = config.getInt("limits.nbt.max-list-elements", 100);
        this.nbtMaxStringLength = config.getInt("limits.nbt.max-string-length", 1024);
        this.nbtMaxBookPages = config.getInt("limits.nbt.max-book-pages", 50);
        this.nbtMaxBookPageLength = config.getInt("limits.nbt.max-book-page-length", 300);
        this.nbtMaxTotalSizeBytes = config.getInt("limits.nbt.max-total-size-bytes", 16384);
        this.signMaxLineLength = config.getInt("limits.sign.max-line-length", 384);
        this.signMaxJsonDepth = config.getInt("limits.sign.max-json-depth", 5);
        this.signMaxPacketsPerSecond = config.getInt("limits.sign.max-packets-per-second", 6);
        this.movementMaxPitch = config.getDouble("limits.movement.max-pitch", 90.0);
        this.movementMinPitch = config.getDouble("limits.movement.min-pitch", -90.0);
        this.movementMaxCoordinate = config.getDouble("limits.movement.max-coordinate", 3.0E7);
        this.movementMaxDeltaDistance = config.getDouble("limits.movement.max-delta-distance", 80.0);
        this.movementMaxPacketsPerSecond = config.getInt("limits.movement.max-packets-per-second", 80);
        this.windowMaxWindowId = config.getInt("limits.window.max-window-id", 255);
        this.windowMaxSlotId = config.getInt("limits.window.max-slot-id", 250);
        this.windowMaxClicksPerSecond = config.getInt("limits.window.max-clicks-per-second", 30);
        this.windowMaxTransactionsPerSecond = config.getInt("limits.window.max-transactions-per-second", 30);
        this.windowMaxClosePerSecond = config.getInt("limits.window.max-close-per-second", 15);
        this.rateKeepAlive = config.getInt("limits.rates.keepalive", 6);
        this.rateClientStatus = config.getInt("limits.rates.client-status", 5);
        this.rateBlockPlace = config.getInt("limits.rates.block-place", 30);
        this.rateBlockDig = config.getInt("limits.rates.block-dig", 35);
        this.rateEntityAction = config.getInt("limits.rates.entity-action", 25);
        this.rateUseEntity = config.getInt("limits.rates.use-entity", 30);
        this.rateVehicleInput = config.getInt("limits.rates.vehicle-input", 25);
        this.rateClientSettings = config.getInt("limits.rates.client-settings", 5);
        this.rateResourcePack = config.getInt("limits.rates.resource-pack", 5);
        this.rateChat = config.getInt("limits.rates.chat", 8);
    }

    public String getPrefix() {
        return this.prefix;
    }

    public String getAlertMessage() {
        return this.alertMessage;
    }

    public String getKickMessage() {
        return this.kickMessage;
    }

    public String getNoPermissionMessage() {
        return this.noPermissionMessage;
    }

    public String getReloadMessage() {
        return this.reloadMessage;
    }

    public List<String> getHelpMessage() {
        return this.helpMessage;
    }

    public String getAlertsEnabledMessage() { return this.alertsEnabledMessage; }
    public String getAlertsDisabledMessage() { return this.alertsDisabledMessage; }
    public String getPlayerOnlyMessage() { return this.playerOnlyMessage; }
    public String getStatsHeader() { return this.statsHeader; }
    public String getStatsChecks() { return this.statsChecks; }
    public String getStatsPlayers() { return this.statsPlayers; }
    public String getStatsRuntime() { return this.statsRuntime; }
    public boolean isBroadcastAlerts() {
        return this.broadcastAlerts;
    }

    public String getAlertPermission() {
        return this.alertPermission;
    }

    public boolean isLogToConsole() {
        return this.logToConsole;
    }

    public long getAlertCooldownMs() {
        return this.alertCooldownMs;
    }

    public boolean isAutoKick() {
        return this.autoKick;
    }

    public int getMaxViolations() {
        return this.maxViolations;
    }

    public int getViolationResetSeconds() {
        return this.violationResetSeconds;
    }

    public int getGlobalMaxPacketsPerSecond() {
        return this.globalMaxPacketsPerSecond;
    }

    public int getPayloadMaxBytes() {
        return this.payloadMaxBytes;
    }

    public int getPayloadMaxChannelsRegister() {
        return this.payloadMaxChannelsRegister;
    }

    public int getPayloadMaxRegisterBytes() {
        return this.payloadMaxRegisterBytes;
    }

    public int getPayloadMaxFrequency() {
        return this.payloadMaxFrequency;
    }

    public int getNbtMaxDepth() {
        return this.nbtMaxDepth;
    }

    public int getNbtMaxTags() {
        return this.nbtMaxTags;
    }

    public int getNbtMaxListElements() {
        return this.nbtMaxListElements;
    }

    public int getNbtMaxStringLength() {
        return this.nbtMaxStringLength;
    }

    public int getNbtMaxBookPages() {
        return this.nbtMaxBookPages;
    }

    public int getNbtMaxBookPageLength() {
        return this.nbtMaxBookPageLength;
    }

    public int getNbtMaxTotalSizeBytes() {
        return this.nbtMaxTotalSizeBytes;
    }

    public int getSignMaxLineLength() {
        return this.signMaxLineLength;
    }

    public int getSignMaxJsonDepth() {
        return this.signMaxJsonDepth;
    }

    public int getSignMaxPacketsPerSecond() {
        return this.signMaxPacketsPerSecond;
    }

    public double getMovementMaxPitch() {
        return this.movementMaxPitch;
    }

    public double getMovementMinPitch() {
        return this.movementMinPitch;
    }

    public double getMovementMaxCoordinate() {
        return this.movementMaxCoordinate;
    }

    public double getMovementMaxDeltaDistance() {
        return this.movementMaxDeltaDistance;
    }

    public int getMovementMaxPacketsPerSecond() {
        return this.movementMaxPacketsPerSecond;
    }

    public int getWindowMaxWindowId() {
        return this.windowMaxWindowId;
    }

    public int getWindowMaxSlotId() {
        return this.windowMaxSlotId;
    }

    public int getWindowMaxClicksPerSecond() {
        return this.windowMaxClicksPerSecond;
    }

    public int getWindowMaxTransactionsPerSecond() {
        return this.windowMaxTransactionsPerSecond;
    }

    public int getWindowMaxClosePerSecond() {
        return this.windowMaxClosePerSecond;
    }

    public int getRateKeepAlive() {
        return this.rateKeepAlive;
    }

    public int getRateClientStatus() {
        return this.rateClientStatus;
    }

    public int getRateBlockPlace() {
        return this.rateBlockPlace;
    }

    public int getRateBlockDig() {
        return this.rateBlockDig;
    }

    public int getRateEntityAction() {
        return this.rateEntityAction;
    }

    public int getRateUseEntity() {
        return this.rateUseEntity;
    }

    public int getRateVehicleInput() {
        return this.rateVehicleInput;
    }

    public int getRateClientSettings() {
        return this.rateClientSettings;
    }

    public int getRateResourcePack() {
        return this.rateResourcePack;
    }

    public int getRateChat() {
        return this.rateChat;
    }
}

