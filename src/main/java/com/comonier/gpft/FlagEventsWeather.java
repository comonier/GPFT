package com.comonier.gpft;

import me.ryanhamshire.GriefPrevention.Claim;
import org.bukkit.WeatherType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.Location;

public class FlagEventsWeather implements Listener {

	private final FlagManager flagManager;

	public FlagEventsWeather(FlagManager flagManager) {
		this.flagManager = flagManager;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerMove(PlayerMoveEvent event) {
		Location from = event.getFrom();
		Location to = event.getTo();

		if (to == null) return;
		if (from.getBlockX() == to.getBlockX() && from.getBlockZ() == to.getBlockZ()) return;

		updateWeather(event.getPlayer(), to);
	}

	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent event) {
		updateWeather(event.getPlayer(), event.getPlayer().getLocation());
	}

	private void updateWeather(Player player, Location loc) {
		Claim claim = flagManager.getClaimAt(loc);

		if (claim == null) {
			player.resetPlayerWeather();
			return;
		}

		// flag weather_lock: true = permite chuva / false = trava em sol
		boolean allowRain = flagManager.getFlagState(claim, "weather_lock");

		if (allowRain == false) {
			// Força o clima visual de SOL para o jogador
			if (player.getPlayerWeather() != WeatherType.CLEAR) {
				player.setPlayerWeather(WeatherType.CLEAR);
			}
		} else {
			// Se a flag permitir chuva, o jogador segue o clima global do mundo
			player.resetPlayerWeather();
		}
	}
}
