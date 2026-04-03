package com.comonier.gpft;

import me.ryanhamshire.GriefPrevention.Claim;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CommandHandler implements CommandExecutor {

	private final Main plugin;
	private final FlagManager flagManager;
	private final MenuManager menuManager;

	public CommandHandler(Main plugin, FlagManager flagManager, MenuManager menuManager) {
		this.plugin = plugin;
		this.flagManager = flagManager;
		this.menuManager = menuManager;
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
		
		// Lógica de Reload (Funciona para Jogadores com permissão E para o Console)
		if (args.length >= 1 && args[0].equalsIgnoreCase("reload")) {
			if (sender.hasPermission("gpft.admin") || (sender instanceof Player == false)) {
				plugin.reloadConfig();
				plugin.loadLocalization();
				plugin.loadModules();
				sender.sendMessage(plugin.getMsg("plugin_reloaded"));
			} else {
				sender.sendMessage(plugin.getMsg("no_permission"));
			}
			return true;
		}

		// A partir daqui, as outras funções exigem que seja um Jogador
		if (sender instanceof Player == false) {
			sender.sendMessage(plugin.getMsg("only_players"));
			return true;
		}

		Player player = (Player) sender;

		// Lógica de Ajuda
		if (args.length >= 1 && args[0].equalsIgnoreCase("help")) {
			sendHelp(player);
			return true;
		}

		// Para abrir o menu, precisa estar em um terreno
		Claim claim = flagManager.getClaimAt(player.getLocation());

		if (claim == null) {
			player.sendMessage(plugin.getMsg("not_in_claim"));
			return true;
		}

		if (flagManager.canManage(player, claim) == false) {
			player.sendMessage(plugin.getMsg("not_owner"));
			return true;
		}

		menuManager.openMenu(player, claim);
		return true;
	}

	private void sendHelp(Player player) {
		player.sendMessage(plugin.getMsg("help_header"));
		player.sendMessage(plugin.getMsg("help_gui"));
		player.sendMessage(plugin.getMsg("help_reload"));
		player.sendMessage(plugin.getMsg("help_footer"));
	}
}
