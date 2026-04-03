package com.comonier.gpft;

import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import me.ryanhamshire.GriefPrevention.Claim;

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
        
        if (clicked == null || clicked.getItemMeta() == null) return;

        NamespacedKey key = new NamespacedKey(plugin, "flag_key");
        String flagKey = clicked.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING);

        if (flagKey != null) {
            Claim claim = flagManager.getClaimAt(player.getLocation());
            if (claim == null) return;

            // Pega o estado do nosso arquivo data.yml
            boolean currentState = flagManager.getFlagState(claim, flagKey);
            
            // Inverte e salva no nosso arquivo
            flagManager.setFlagState(claim, flagKey, !currentState);
            
            player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING, 0.5f, 2f);
            
            // Reabre o menu. Como o dado está no nosso arquivo, a mudança é INSTANTÂNEA.
            menuManager.openMenu(player, claim);
        }
    }
}
