package com.comonier.gpft;

import me.ryanhamshire.GriefPrevention.Claim;
import org.bukkit.Location;
import org.bukkit.WeatherType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class FlagEventsWeather implements Listener {
   private final FlagManager flagManager;

   public FlagEventsWeather(FlagManager flagManager) {
      this.flagManager = flagManager;
   }

   @EventHandler(
      priority = EventPriority.MONITOR,
      ignoreCancelled = true
   )
   public void onPlayerMove(PlayerMoveEvent event) {
      Location from = event.getFrom();
      Location to = event.getTo();
      if (to != null) {
         if (from.getBlockX() != to.getBlockX() || from.getBlockZ() != to.getBlockZ()) {
            this.updateWeather(event.getPlayer(), to);
         }
      }
   }

   @EventHandler
   public void onWorldChange(PlayerChangedWorldEvent event) {
      this.updateWeather(event.getPlayer(), event.getPlayer().getLocation());
   }

   private void updateWeather(Player player, Location loc) {
      Claim claim = this.flagManager.getClaimAt(loc);
      if (claim == null) {
         player.resetPlayerWeather();
      } else {
         boolean allowRain = this.flagManager.getFlagState(claim, "weather_lock");
         if (!allowRain) {
            if (player.getPlayerWeather() != WeatherType.CLEAR) {
               player.setPlayerWeather(WeatherType.CLEAR);
            }
         } else {
            player.resetPlayerWeather();
         }

      }
   }
}
