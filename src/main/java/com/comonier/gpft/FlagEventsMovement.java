package com.comonier.gpft;

import com.comonier.gpft.FlagEventsMovement.1;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import me.ryanhamshire.GriefPrevention.Claim;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class FlagEventsMovement implements Listener {
   private final Main plugin;
   private final FlagManager flagManager;
   private final Map<UUID, Long> lastClaim = new HashMap();
   private final Map<UUID, BossBar> bossBars = new HashMap();

   public FlagEventsMovement(Main plugin, FlagManager flagManager) {
      this.plugin = plugin;
      this.flagManager = flagManager;
   }

   @EventHandler(
      priority = EventPriority.MONITOR,
      ignoreCancelled = true
   )
   public void onMove(PlayerMoveEvent event) {
      Location from = event.getFrom();
      Location to = event.getTo();
      if (to != null && (from.getBlockX() != to.getBlockX() || from.getBlockZ() != to.getBlockZ())) {
         Player player = event.getPlayer();
         Claim claim = this.flagManager.getClaimAt(to);
         Long currentId = claim != null ? claim.getID() : -1L;
         Long previousId = (Long)this.lastClaim.getOrDefault(player.getUniqueId(), -1L);
         if (!currentId.equals(previousId)) {
            this.lastClaim.put(player.getUniqueId(), currentId);
            if (claim != null) {
               this.sendEntryNotification(player, claim);
            } else {
               this.removeBossBar(player);
            }

         }
      }
   }

   private void sendEntryNotification(Player player, Claim claim) {
      if (this.plugin.getConfig().getBoolean("notifications.enabled", true)) {
         String ownerName = claim.getOwnerName();
         boolean hasTrust = claim.allowAccess(player) == null;
         int seconds = this.plugin.getConfig().getInt("notifications.stay_time", 3);
         int ticks = seconds * 20;
         String msgKey;
         if (this.plugin.getConfig().getBoolean("notifications.use_chat", false)) {
            msgKey = hasTrust ? "entry_has_trust" : "entry_no_trust";
            player.sendMessage(this.plugin.getMsg(msgKey).replace("{owner}", ownerName));
         }

         String rawBoss;
         if (this.plugin.getConfig().getBoolean("notifications.use_actionbar", true)) {
            msgKey = hasTrust ? "entry_has_trust" : "entry_no_trust";
            rawBoss = this.plugin.getMsgRaw(msgKey).replace("{owner}", ownerName);
            (new 1(this, seconds, player, rawBoss)).runTaskTimer(this.plugin, 0L, 20L);
         }

         if (this.plugin.getConfig().getBoolean("notifications.use_title", true)) {
            msgKey = this.plugin.getMsgRaw("title_owner_nick").replace("{owner}", ownerName);
            rawBoss = hasTrust ? "subtitle_has_trust" : "subtitle_no_trust";
            String subtitle = this.plugin.getMsgRaw(rawBoss);
            player.sendTitle(msgKey, subtitle, 10, ticks, 10);
         }

         if (this.plugin.getConfig().getBoolean("notifications.use_bossbar", false)) {
            msgKey = hasTrust ? "entry_has_trust" : "entry_no_trust";
            rawBoss = this.plugin.getMsgRaw(msgKey).replace("{owner}", ownerName);
            this.showBossBar(player, rawBoss, (long)ticks);
         }

      }
   }

   private void showBossBar(Player player, String message, long ticks) {
      this.removeBossBar(player);
      String colorStr = this.plugin.getConfig().getString("notifications.bossbar_color", "YELLOW").toUpperCase();
      String styleStr = this.plugin.getConfig().getString("notifications.bossbar_style", "SOLID").toUpperCase();

      BarColor color;
      try {
         color = BarColor.valueOf(colorStr);
      } catch (Exception var11) {
         color = BarColor.YELLOW;
      }

      BarStyle style;
      try {
         style = BarStyle.valueOf(styleStr);
      } catch (Exception var10) {
         style = BarStyle.SOLID;
      }

      BossBar bar = Bukkit.createBossBar(message, color, style, new BarFlag[0]);
      bar.addPlayer(player);
      this.bossBars.put(player.getUniqueId(), bar);
      Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
         this.removeBossBar(player);
      }, ticks);
   }

   private void removeBossBar(Player player) {
      BossBar bar = (BossBar)this.bossBars.remove(player.getUniqueId());
      if (bar != null) {
         bar.removeAll();
      }

   }
}
