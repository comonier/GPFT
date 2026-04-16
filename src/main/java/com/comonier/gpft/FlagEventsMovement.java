package com.comonier.gpft;

import me.ryanhamshire.GriefPrevention.Claim;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.Location;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.Bukkit;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FlagEventsMovement implements Listener {
    private final Main plugin;
    private final FlagManager flagManager;
    private final Map<UUID, Long> lastClaim = new HashMap<>();
    private final Map<UUID, BossBar> bossBars = new HashMap<>();

    public FlagEventsMovement(Main plugin, FlagManager flagManager) {
        this.plugin = plugin;
        this.flagManager = flagManager;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();
        if (null == to || (from.getBlockX() == to.getBlockX() && from.getBlockZ() == to.getBlockZ())) return;

        Player player = event.getPlayer();
        Claim claim = flagManager.getClaimAt(to);
        Long currentId = (null != claim) ? claim.getID() : -1L;
        Long previousId = lastClaim.getOrDefault(player.getUniqueId(), -1L);

        if (currentId.equals(previousId)) return;
        lastClaim.put(player.getUniqueId(), currentId);

        if (null != claim) {
            sendEntryNotification(player, claim);
        } else {
            removeBossBar(player);
        }
    }

    private boolean isSilenced(Player player, String type) {
        NamespacedKey key = new NamespacedKey(plugin, "hide_notify_" + type);
        return player.getPersistentDataContainer().has(key, PersistentDataType.BYTE);
    }

    private void sendEntryNotification(Player player, Claim claim) {
        if (false == plugin.getConfig().getBoolean("notifications.enabled", true)) return;

        String ownerName = claim.getOwnerName();
        boolean hasFullTrust = (null == claim.allowBuild(player, null));
        int seconds = plugin.getConfig().getInt("notifications.stay_time", 3);
        int ticks = seconds * 20;

        // Chat Notification
        if (plugin.getConfig().getBoolean("notifications.use_chat") && !isSilenced(player, "chat")) {
            String msgKey = hasFullTrust ? "entry_has_trust" : "entry_no_trust";
            player.sendMessage(plugin.getMsg(msgKey).replace("{owner}", ownerName));
        }

        // ActionBar Notification
        if (plugin.getConfig().getBoolean("notifications.use_actionbar") && !isSilenced(player, "action")) {
            String msgKey = hasFullTrust ? "entry_has_trust" : "entry_no_trust";
            String rawAction = plugin.getMsgRaw(msgKey).replace("{owner}", ownerName);
            
            new BukkitRunnable() {
                int count = 0;
                @Override
                public void run() {
                    if (count >= seconds || !player.isOnline() || isSilenced(player, "action")) { 
                        this.cancel(); 
                        return; 
                    }
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(rawAction));
                    count++;
                }
            }.runTaskTimer(plugin, 0L, 20L);
        }

        // Title/Screen Notification
        if (plugin.getConfig().getBoolean("notifications.use_title") && !isSilenced(player, "screen")) {
            String title = plugin.getMsgRaw("title_owner_nick").replace("{owner}", ownerName);
            String subtitleKey = hasFullTrust ? "subtitle_has_trust" : "subtitle_no_trust";
            String subtitle = plugin.getMsgRaw(subtitleKey);
            player.sendTitle(title, subtitle, 10, ticks, 10);
        }

        // BossBar Notification
        if (plugin.getConfig().getBoolean("notifications.use_bossbar") && !isSilenced(player, "boss")) {
            String msgKey = hasFullTrust ? "entry_has_trust" : "entry_no_trust";
            String rawBoss = plugin.getMsgRaw(msgKey).replace("{owner}", ownerName);
            showBossBar(player, rawBoss, (long) ticks);
        }
    }

    private void showBossBar(Player player, String message, long ticks) {
        removeBossBar(player);
        if (isSilenced(player, "boss")) return;

        String colorStr = plugin.getConfig().getString("notifications.bossbar_color", "YELLOW").toUpperCase();
        String styleStr = plugin.getConfig().getString("notifications.bossbar_style", "SOLID").toUpperCase();
        
        BarColor color = BarColor.YELLOW;
        try { color = BarColor.valueOf(colorStr); } catch (Exception ignored) {}
        BarStyle style = BarStyle.SOLID;
        try { style = BarStyle.valueOf(styleStr); } catch (Exception ignored) {}

        BossBar bar = Bukkit.createBossBar(message, color, style);
        bar.addPlayer(player);
        bossBars.put(player.getUniqueId(), bar);
        Bukkit.getScheduler().runTaskLater(plugin, () -> removeBossBar(player), ticks);
    }

    private void removeBossBar(Player player) {
        BossBar bar = bossBars.remove(player.getUniqueId());
        if (null != bar) bar.removeAll();
    }
}
