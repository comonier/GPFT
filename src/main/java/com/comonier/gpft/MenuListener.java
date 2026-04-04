package com.comonier.gpft;

import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.Bukkit;
import me.ryanhamshire.GriefPrevention.Claim;
import org.bukkit.metadata.FixedMetadataValue;

public class MenuListener implements Listener {
    private final Main plugin;
    private final FlagManager flagManager;
    private final MenuManager menuManager;

    public MenuListener(Main plugin, FlagManager flagManager, MenuManager menuManager) {
        this.plugin = plugin;
        this.flagManager = flagManager;
        this.menuManager = menuManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof MenuManager == false) return;
        
        event.setCancelled(true);
        if (event.getWhoClicked() instanceof Player == false) return;
        
        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();
        
        if (null == clicked || null == clicked.getItemMeta()) return;

        NamespacedKey key = new NamespacedKey(plugin, "flag_key");
        String flagKey = clicked.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING);

        if (null != flagKey) {
            boolean isEnabled = plugin.isModuleEnabled(flagKey);
            boolean hasPerm = player.hasPermission("gpft.flag." + flagKey) || player.hasPermission("gpft.admin");
            
            if ((false == isEnabled && false == player.hasPermission("gpft.admin")) || false == hasPerm) return;

            Claim claim = flagManager.getClaimAt(player.getLocation());
            if (null == claim) return;

            boolean newState = !flagManager.getFlagState(claim, flagKey);
            flagManager.setFlagState(claim, flagKey, newState);
            
            player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING, 0.5f, 2f);

            if (flagKey.equals("visitor_fly")) {
                syncFlyForClaim(claim, newState);
            }
            
            menuManager.openMenu(player, claim);
        }
    }

    private void syncFlyForClaim(Claim claim, boolean active) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (claim.contains(p.getLocation(), true, false)) {
                if (p.hasPermission("essentials.fly") || p.getGameMode().name().contains("CREATIVE")) continue;

                // Verificação rigorosa: Builder Trust ou superior
                if (active && null == claim.allowBuild(p, null)) {
                    p.setAllowFlight(true);
                    p.setMetadata("gpft_fly_active", new FixedMetadataValue(plugin, true));
                    p.sendTitle(plugin.getMsgRaw("fly_on_title"), plugin.getMsgRaw("fly_on_subtitle"), 10, 40, 10);
                }
            }
        }
    }
}
