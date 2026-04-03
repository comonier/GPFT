package com.comonier.gpft;

import me.ryanhamshire.GriefPrevention.Claim;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.entity.Animals;

public class FlagEventsTrust implements Listener {
	private final FlagManager flagManager;

	public FlagEventsTrust(FlagManager flagManager) {
		this.flagManager = flagManager;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onVisitorInteract(PlayerInteractEvent event) {
		if (event.getClickedBlock() == null) return;
		Player player = event.getPlayer();
		Material block = event.getClickedBlock().getType();
		String name = block.name();
		Claim claim = flagManager.getClaimAt(event.getClickedBlock().getLocation());
		
		if (claim == null || claim.allowAccess(player) == null) return;

		boolean allowed = false;
		boolean isMatch = false;

		if (name.contains("BUTTON") || block == Material.LEVER) {
			isMatch = true;
			allowed = flagManager.getFlagState(claim, "buttons_levers");
		} else if (event.getAction() == Action.PHYSICAL && name.contains("PRESSURE_PLATE")) {
			isMatch = true;
			allowed = flagManager.getFlagState(claim, "pressure_plates");
		} else if (isWoodenDoor(name)) {
			isMatch = true;
			allowed = flagManager.getFlagState(claim, "wooden_doors");
		} else if (name.contains("IRON_DOOR") || name.contains("IRON_TRAPDOOR")) {
			isMatch = true;
			allowed = flagManager.getFlagState(claim, "iron_doors");
		} else if (block == Material.CHEST || block == Material.TRAPPED_CHEST || block == Material.BARREL || name.contains("SHULKER_BOX")) {
			isMatch = true;
			allowed = flagManager.getFlagState(claim, "open_chests");
		} else if (block == Material.ENDER_CHEST) {
			isMatch = true;
			allowed = flagManager.getFlagState(claim, "ender_chests");
		}

		if (isMatch) {
			event.setCancelled(!allowed);
			// Se permitimos, tentamos silenciar o GP marcando o evento como ja processado
			if (allowed) {
				player.setMetadata("gpft_allowed", new org.bukkit.metadata.FixedMetadataValue(Main.getInstance(), true));
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityInteract(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();
		Claim claim = flagManager.getClaimAt(event.getRightClicked().getLocation());
		if (claim == null || claim.allowAccess(player) == null) return;

		boolean allowed = false;
		boolean isMatch = false;

		if (event.getRightClicked() instanceof Villager) {
			isMatch = true;
			allowed = flagManager.getFlagState(claim, "villager_trade");
		} else if (event.getRightClicked() instanceof Animals) {
			isMatch = true;
			allowed = flagManager.getFlagState(claim, "lead_mobs");
		}

		if (isMatch) {
			event.setCancelled(!allowed);
			if (allowed) {
				player.setMetadata("gpft_allowed", new org.bukkit.metadata.FixedMetadataValue(Main.getInstance(), true));
			}
		}
	}

	private boolean isWoodenDoor(String name) {
		if (name.contains("IRON")) return false;
		return name.contains("DOOR") || name.contains("GATE") || name.contains("TRAPDOOR");
	}
}
