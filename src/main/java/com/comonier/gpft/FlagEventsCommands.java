package com.comonier.gpft;

import me.ryanhamshire.GriefPrevention.Claim;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/*
 * FlagEventsCommands for GPFT
 * Manages flags related to commands, such as /sethome
 */
public class FlagEventsCommands implements Listener {

    private final FlagManager flagManager;
    private final Main plugin;

    public FlagEventsCommands(Main plugin, FlagManager flagManager) {
        this.plugin = plugin;
        this.flagManager = flagManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onSetHome(PlayerCommandPreprocessEvent event) {
        String cmd = event.getMessage().toLowerCase();
        
        // Check if the command is /sethome
        if (cmd.startsWith("/sethome") == false) return;

        Player player = event.getPlayer();
        Claim claim = flagManager.getClaimAt(player.getLocation());

        // Outside of claims, we don't interfere
        if (claim == null) return;

        // If player has trust, they are allowed by default
        if (claim.allowAccess(player) == null) return;

        // Check if set_home flag is disabled for visitors (false)
        if (flagManager.getFlagState(claim, "set_home") == false) {
            event.setCancelled(true);
            player.sendMessage(plugin.getMsg("no_permission"));
        }
    }
}
