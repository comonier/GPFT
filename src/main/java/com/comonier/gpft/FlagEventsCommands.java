package com.comonier.gpft;

import me.ryanhamshire.GriefPrevention.Claim;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class FlagEventsCommands implements Listener {
   private final FlagManager flagManager;
   private final Main plugin;

   public FlagEventsCommands(Main plugin, FlagManager flagManager) {
      this.plugin = plugin;
      this.flagManager = flagManager;
   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public void onSetHome(PlayerCommandPreprocessEvent event) {
      String cmd = event.getMessage().toLowerCase();
      if (cmd.startsWith("/sethome")) {
         Player player = event.getPlayer();
         Claim claim = this.flagManager.getClaimAt(player.getLocation());
         if (claim != null) {
            if (claim.allowAccess(player) != null) {
               if (!this.flagManager.getFlagState(claim, "set_home")) {
                  event.setCancelled(true);
                  player.sendMessage(this.plugin.getMsg("no_permission"));
               }

            }
         }
      }
   }
}
