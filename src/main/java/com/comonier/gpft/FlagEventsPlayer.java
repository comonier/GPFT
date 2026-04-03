package com.comonier.gpft;

import me.ryanhamshire.GriefPrevention.Claim;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

public class FlagEventsPlayer implements Listener {
	private final FlagManager flagManager;
	private final Main plugin;

	public FlagEventsPlayer(Main plugin, FlagManager flagManager) {
		this.plugin = plugin;
		this.flagManager = flagManager;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBowInteract(PlayerInteractEvent event) {
		if (event.getAction() == Action.PHYSICAL) return;
		ItemStack item = event.getItem();
		if (item == null) return;

		Material type = item.getType();
		if (type == Material.BOW || type == Material.CROSSBOW) {
			Player player = event.getPlayer();
			Claim claim = flagManager.getClaimAt(player.getLocation());
			if (claim == null || claim.allowAccess(player) == null) return;

			if (flagManager.getFlagState(claim, "use_bows") == false) {
				event.setCancelled(true);
				player.sendMessage(plugin.getMsg("use_bow_denied"));
			} else {
				event.setCancelled(false);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEnderPearl(PlayerTeleportEvent event) {
		if (event.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) return;
		
		Claim claim = flagManager.getClaimAt(event.getTo());
		if (claim == null || claim.allowAccess(event.getPlayer()) == null) return;

		if (flagManager.getFlagState(claim, "ender_pearl")) {
			event.setCancelled(false);
		} else {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDropItem(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		Claim claim = flagManager.getClaimAt(player.getLocation());
		if (claim == null || claim.allowAccess(player) == null) return;

		event.setCancelled(!flagManager.getFlagState(claim, "item_drop"));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPickupItem(EntityPickupItemEvent event) {
		if (event.getEntity() instanceof Player == false) return;
		Player player = (Player) event.getEntity();
		Claim claim = flagManager.getClaimAt(player.getLocation());
		if (claim == null || claim.allowAccess(player) == null) return;

		event.setCancelled(!flagManager.getFlagState(claim, "pickup_items"));
	}
}
