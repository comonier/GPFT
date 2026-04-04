package com.comonier.gpft;

import me.ryanhamshire.GriefPrevention.Claim;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class MenuListener implements Listener {
   private final Main plugin;
   private final FlagManager flagManager;
   private final MenuManager menuManager;

   public MenuListener(Main plugin, FlagManager flagManager, MenuManager menuManager) {
      this.plugin = plugin;
      this.flagManager = flagManager;
      this.menuManager = menuManager;
   }

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void onInventoryClick(InventoryClickEvent event) {
      if (event.getInventory().getHolder() instanceof MenuManager) {
         event.setCancelled(true);
         if (event.getWhoClicked() instanceof Player) {
            Player player = (Player)event.getWhoClicked();
            ItemStack clicked = event.getCurrentItem();
            if (clicked != null && clicked.getItemMeta() != null) {
               NamespacedKey key = new NamespacedKey(this.plugin, "flag_key");
               String flagKey = (String)clicked.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING);
               if (flagKey != null) {
                  boolean isEnabled = this.plugin.isModuleEnabled(flagKey);
                  boolean hasPerm = player.hasPermission("gpft.flag." + flagKey) || player.hasPermission("gpft.admin");
                  if (!isEnabled && !player.hasPermission("gpft.admin") || !hasPerm) {
                     return;
                  }

                  Claim claim = this.flagManager.getClaimAt(player.getLocation());
                  if (claim == null) {
                     return;
                  }

                  boolean currentState = this.flagManager.getFlagState(claim, flagKey);
                  this.flagManager.setFlagState(claim, flagKey, !currentState);
                  player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.5F, 2.0F);
                  this.menuManager.openMenu(player, claim);
               }

            }
         }
      }
   }
}
