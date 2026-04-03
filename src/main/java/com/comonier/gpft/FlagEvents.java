package com.comonier.gpft;

import me.ryanhamshire.GriefPrevention.Claim;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Animals;

public class FlagEvents implements Listener {
	private final FlagManager flagManager;

	public FlagEvents(FlagManager flagManager) {
		this.flagManager = flagManager;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onMobDamage(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player == false) return;
		Player player = (Player) event.getDamager();
		
		if (event.getEntity() instanceof Animals == false && event.getEntity() instanceof Monster == false) return;
		
		Claim claim = flagManager.getClaimAt(event.getEntity().getLocation());
		if (claim == null || claim.allowAccess(player) == null) return;

		if (flagManager.getFlagState(claim, "kill_mobs")) {
			event.setCancelled(false);
		} else {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onMonsterSpawn(CreatureSpawnEvent event) {
		if (event.getEntity() instanceof Monster == false) return;
		Claim claim = flagManager.getClaimAt(event.getLocation());
		if (claim == null) return;
		if (flagManager.getFlagState(claim, "monster_spawn") == false) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPassiveSpawn(CreatureSpawnEvent event) {
		if (event.getEntity() instanceof Animals == false) return;
		Claim claim = flagManager.getClaimAt(event.getLocation());
		if (claim == null) return;
		if (flagManager.getFlagState(claim, "passive_spawn") == false) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onLeafDecay(LeavesDecayEvent event) {
		Claim claim = flagManager.getClaimAt(event.getBlock().getLocation());
		if (claim == null) return;
		if (flagManager.getFlagState(claim, "leaf_decay") == false) {
			event.setCancelled(true);
		}
	}
}
