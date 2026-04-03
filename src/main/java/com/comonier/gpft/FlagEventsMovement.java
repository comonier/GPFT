// path: src/main/java/com/comonier/gpft/FlagEventsMovement.java

package com.comonier.gpft;

import me.ryanhamshire.GriefPrevention.Claim;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.Location;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FlagEventsMovement implements Listener {
	private final Main plugin;
	private final FlagManager flagManager;
	private final Map<UUID, Long> lastClaim = new HashMap<>();
	private final Map<UUID, BossBar> bossBars = new HashMap<>();

	public FlagEventsMovement(Main plugin, FlagManager flagManager) {
		this.plugin = plugin;
		this.flagManager = flagManager;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onMove(PlayerMoveEvent event) {
		Location from = event.getFrom();
		Location to = event.getTo();
		if (to == null || (from.getBlockX() == to.getBlockX() && from.getBlockZ() == to.getBlockZ())) return;

		Player player = event.getPlayer();
		Claim claim = flagManager.getClaimAt(to);
		Long currentId = (claim != null) ? claim.getID() : -1L;
		Long previousId = lastClaim.getOrDefault(player.getUniqueId(), -1L);

		if (currentId.equals(previousId)) return;
		lastClaim.put(player.getUniqueId(), currentId);

		if (claim != null) {
			sendEntryNotification(player, claim);
		} else {
			removeBossBar(player);
		}
	}

	private void sendEntryNotification(Player player, Claim claim) {
		if (plugin.getConfig().getBoolean("notifications.enabled", true) == false) return;

		String ownerName = claim.getOwnerName();
		boolean hasTrust = claim.allowAccess(player) == null;
		
		int seconds = plugin.getConfig().getInt("notifications.stay_time", 3);
		int ticks = seconds * 20;

		// 1. Lógica do CHAT
		if (plugin.getConfig().getBoolean("notifications.use_chat", false)) {
			String msgKey = hasTrust ? "entry_has_trust" : "entry_no_trust";
			player.sendMessage(plugin.getMsg(msgKey).replace("{owner}", ownerName));
		}

		// 2. Lógica da ACTION BAR
		if (plugin.getConfig().getBoolean("notifications.use_actionbar", true)) {
			String msgKey = hasTrust ? "entry_has_trust" : "entry_no_trust";
			String rawAction = plugin.getMsgRaw(msgKey).replace("{owner}", ownerName);
			new BukkitRunnable() {
				int count = 0;
				@Override
				public void run() {
					if (count >= seconds || player.isOnline() == false) { this.cancel(); return; }
					player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(rawAction));
					count++;
				}
			}.runTaskTimer(plugin, 0L, 20L);
		}

		// 3. Lógica do TITLE (Macete: Nick no Title, Status no Subtitle)
		if (plugin.getConfig().getBoolean("notifications.use_title", true)) {
			String title = plugin.getMsgRaw("title_owner_nick").replace("{owner}", ownerName);
			String subtitleKey = hasTrust ? "subtitle_has_trust" : "subtitle_no_trust";
			String subtitle = plugin.getMsgRaw(subtitleKey);
			player.sendTitle(title, subtitle, 10, ticks, 10);
		}

		// 4. Lógica da BOSS BAR
		if (plugin.getConfig().getBoolean("notifications.use_bossbar", false)) {
			String msgKey = hasTrust ? "entry_has_trust" : "entry_no_trust";
			String rawBoss = plugin.getMsgRaw(msgKey).replace("{owner}", ownerName);
			showBossBar(player, rawBoss, (long) ticks);
		}
	}

	private void showBossBar(Player player, String message, long ticks) {
		removeBossBar(player);
		
		String colorStr = plugin.getConfig().getString("notifications.bossbar_color", "YELLOW").toUpperCase();
		String styleStr = plugin.getConfig().getString("notifications.bossbar_style", "SOLID").toUpperCase();
		
		BarColor color;
		try {
			color = BarColor.valueOf(colorStr);
		} catch (Exception e) {
			color = BarColor.YELLOW;
		}
		
		BarStyle style;
		try {
			style = BarStyle.valueOf(styleStr);
		} catch (Exception e) {
			style = BarStyle.SOLID;
		}

		BossBar bar = Bukkit.createBossBar(message, color, style);
		bar.addPlayer(player);
		bossBars.put(player.getUniqueId(), bar);
		Bukkit.getScheduler().runTaskLater(plugin, () -> removeBossBar(player), ticks);
	}

	private void removeBossBar(Player player) {
		BossBar bar = bossBars.remove(player.getUniqueId());
		if (bar != null) bar.removeAll();
	}
}
