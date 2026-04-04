package com.comonier.gpft;

import me.ryanhamshire.GriefPrevention.Claim;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

public class FlagEventsSpawning implements Listener {
   private final FlagManager flagManager;

   public FlagEventsSpawning(FlagManager flagManager) {
      this.flagManager = flagManager;
   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public void onSpawnerSpawn(CreatureSpawnEvent event) {
      if (event.getSpawnReason() == SpawnReason.SPAWNER) {
         Claim claim = this.flagManager.getClaimAt(event.getLocation());
         if (claim != null) {
            if (!this.flagManager.getFlagState(claim, "spawner_spawn")) {
               event.setCancelled(true);
            }

         }
      }
   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public void onEggSpawn(CreatureSpawnEvent event) {
      SpawnReason reason = event.getSpawnReason();
      if (reason != SpawnReason.SPAWNER_EGG) {
         boolean isEgg = false;
         if (reason == SpawnReason.EGG) {
            isEgg = true;
         }

         if (reason == SpawnReason.DISPENSE_EGG) {
            isEgg = true;
         }

         if (isEgg) {
            Claim claim = this.flagManager.getClaimAt(event.getLocation());
            if (claim != null) {
               if (!this.flagManager.getFlagState(claim, "egg_spawn")) {
                  event.setCancelled(true);
               }

            }
         }
      }
   }
}
