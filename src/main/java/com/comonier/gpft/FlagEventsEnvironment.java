package com.comonier.gpft;

import me.ryanhamshire.GriefPrevention.Claim;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

public class FlagEventsEnvironment implements Listener {
   private final FlagManager flagManager;

   public FlagEventsEnvironment(FlagManager flagManager) {
      this.flagManager = flagManager;
   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public void onLiquidFlow(BlockFromToEvent event) {
      Claim claim = this.flagManager.getClaimAt(event.getToBlock().getLocation());
      if (claim != null) {
         if (!this.flagManager.getFlagState(claim, "liquid_flow")) {
            event.setCancelled(true);
         }

      }
   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public void onExplosion(EntityExplodeEvent event) {
      Claim claim = this.flagManager.getClaimAt(event.getLocation());
      if (claim != null) {
         if (!this.flagManager.getFlagState(claim, "explosions")) {
            event.setCancelled(true);
            event.blockList().clear();
         }

      }
   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public void onBlockBurn(BlockBurnEvent event) {
      Claim claim = this.flagManager.getClaimAt(event.getBlock().getLocation());
      if (claim != null) {
         if (!this.flagManager.getFlagState(claim, "fire_spread")) {
            event.setCancelled(true);
         }

      }
   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public void onIceSnowForm(BlockFormEvent event) {
      Material type = event.getNewState().getType();
      if (type == Material.ICE || type == Material.SNOW) {
         Claim claim = this.flagManager.getClaimAt(event.getBlock().getLocation());
         if (claim == null) {
            return;
         }

         if (!this.flagManager.getFlagState(claim, "ice_form")) {
            event.setCancelled(true);
         }
      }

   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public void onGrassSpread(BlockSpreadEvent event) {
      if (event.getSource().getType() == Material.GRASS_BLOCK) {
         Claim claim = this.flagManager.getClaimAt(event.getBlock().getLocation());
         if (claim == null) {
            return;
         }

         if (!this.flagManager.getFlagState(claim, "grass_spread")) {
            event.setCancelled(true);
         }
      }

   }
}
