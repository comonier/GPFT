package com.comonier.gpft;

import me.ryanhamshire.GriefPrevention.Claim;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

/*
 * Updated FlagEventsSpawning for GPFT
 * Fixed SpawnReason compatibility for 1.21.1
 */
public class FlagEventsSpawning implements Listener {

    private final FlagManager flagManager;

    public FlagEventsSpawning(FlagManager flagManager) {
        this.flagManager = flagManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onSpawnerSpawn(CreatureSpawnEvent event) {
        // Only target Spawners
        if (event.getSpawnReason() == SpawnReason.SPAWNER == false) return;

        Claim claim = flagManager.getClaimAt(event.getLocation());
        if (claim == null) return;

        if (flagManager.getFlagState(claim, "spawner_spawn") == false) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEggSpawn(CreatureSpawnEvent event) {
        SpawnReason reason = event.getSpawnReason();
        
        // In 1.21, EGG covers both player and dispenser egg usage
        boolean isEgg = false;
        if (reason == SpawnReason.EGG) isEgg = true;
        if (reason == SpawnReason.DISPENSE_EGG) isEgg = true;
        
        if (isEgg == false) return;

        Claim claim = flagManager.getClaimAt(event.getLocation());
        if (claim == null) return;

        if (flagManager.getFlagState(claim, "egg_spawn") == false) {
            event.setCancelled(true);
        }
    }
}
