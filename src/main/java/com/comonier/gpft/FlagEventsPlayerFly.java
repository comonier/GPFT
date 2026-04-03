package com.comonier.gpft;

import me.ryanhamshire.GriefPrevention.Claim;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.GameMode;
import org.bukkit.Location;

public class FlagEventsPlayerFly implements Listener {

    private final FlagManager flagManager;

    public FlagEventsPlayerFly(FlagManager flagManager) {
        this.flagManager = flagManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMove(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();

        if (to == null) return;

        // Only run if the player moved to a different block
        if (from.getBlockX() == to.getBlockX() && from.getBlockZ() == to.getBlockZ()) {
            return;
        }

        Player player = event.getPlayer();

        // Skip for creative or spectator
        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) {
            return;
        }

        Claim claim = flagManager.getClaimAt(to);

        // CASE 1: Outside of a claim
        if (claim == null) {
            if (player.getAllowFlight() && player.hasPermission("gpft.admin") == false) {
                player.setAllowFlight(false);
                player.setFlying(false);
            }
            return;
        }

        // CASE 2: Inside a claim
        boolean canFly = flagManager.getFlagState(claim, "visitor_fly");

        if (canFly) {
            // Force allow flight even if GP tried to disable it
            if (player.getAllowFlight() == false) {
                player.setAllowFlight(true);
            }
        } else {
            // Disable flight if flag is OFF and player is not owner/admin
            if (player.getAllowFlight()) {
                if (player.getUniqueId().equals(claim.ownerID) == false && player.hasPermission("gpft.admin") == false) {
                    player.setAllowFlight(false);
                    player.setFlying(false);
                }
            }
        }
    }
}
