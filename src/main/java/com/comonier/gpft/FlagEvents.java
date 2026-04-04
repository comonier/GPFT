package com.comonier.gpft;

import me.ryanhamshire.GriefPrevention.Claim;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

public class FlagEvents implements Listener {
   private final FlagManager flagManager;

   public FlagEvents(FlagManager flagManager) {
      this.flagManager = flagManager;
   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public void onMonsterDamagePlayer(EntityDamageByEntityEvent event) {
      if (event.getEntity() instanceof Player) {
         Entity damager = event.getDamager();
         boolean isMonsterDamage = false;
         if (damager instanceof Monster) {
            isMonsterDamage = true;
         } else if (damager instanceof Projectile) {
            Projectile projectile = (Projectile)damager;
            if (projectile.getShooter() instanceof Monster) {
               isMonsterDamage = true;
            }
         }

         if (isMonsterDamage) {
            Claim claim = this.flagManager.getClaimAt(event.getEntity().getLocation());
            if (claim != null) {
               if (!this.flagManager.getFlagState(claim, "monster_damage")) {
                  event.setCancelled(true);
                  if (damager instanceof Projectile) {
                     damager.remove();
                  }
               }

            }
         }
      }
   }

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void onMobDamage(EntityDamageByEntityEvent event) {
      if (event.getDamager() instanceof Player) {
         Player player = (Player)event.getDamager();
         if (!(event.getEntity() instanceof Monster)) {
            if (event.getEntity() instanceof Animals) {
               Claim claim = this.flagManager.getClaimAt(event.getEntity().getLocation());
               if (claim != null && claim.allowAccess(player) != null) {
                  if (this.flagManager.getFlagState(claim, "kill_passive")) {
                     event.setCancelled(false);
                  } else {
                     event.setCancelled(true);
                  }

               }
            }
         }
      }
   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public void onMonsterSpawn(CreatureSpawnEvent event) {
      if (event.getEntity() instanceof Monster) {
         SpawnReason reason = event.getSpawnReason();
         if (reason == SpawnReason.NATURAL || reason == SpawnReason.DEFAULT || reason == SpawnReason.CHUNK_GEN) {
            Claim claim = this.flagManager.getClaimAt(event.getLocation());
            if (claim != null) {
               if (!this.flagManager.getFlagState(claim, "monster_spawn")) {
                  event.setCancelled(true);
               }

            }
         }
      }
   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public void onPassiveSpawn(CreatureSpawnEvent event) {
      if (event.getEntity() instanceof Animals) {
         SpawnReason reason = event.getSpawnReason();
         if (reason == SpawnReason.NATURAL || reason == SpawnReason.DEFAULT || reason == SpawnReason.CHUNK_GEN) {
            Claim claim = this.flagManager.getClaimAt(event.getLocation());
            if (claim != null) {
               if (!this.flagManager.getFlagState(claim, "passive_spawn")) {
                  event.setCancelled(true);
               }

            }
         }
      }
   }

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void onLeafDecay(LeavesDecayEvent event) {
      Claim claim = this.flagManager.getClaimAt(event.getBlock().getLocation());
      if (claim != null) {
         if (!this.flagManager.getFlagState(claim, "leaf_decay")) {
            event.setCancelled(true);
         }

      }
   }
}
