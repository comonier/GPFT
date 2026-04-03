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
        if (sender instanceof Player == false) {
            sender.sendMessage(plugin.getMsg("only_players"));
            return true;
        }

        Player player = (Player) sender;
        Claim claim = flagManager.getClaimAt(player.getLocation());

        if (claim == null) {
            player.sendMessage(plugin.getMsg("not_in_claim"));
            return true;
        }

        if (flagManager.canManage(player, claim) == false) {
            player.sendMessage(plugin.getMsg("not_owner"));
            return true;
        }

        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("help")) {
                sendHelp(player);
                return true;
            }
            if (args[0].equalsIgnoreCase("reload") && player.hasPermission("gpft.admin")) {
                plugin.reloadConfig();
                plugin.loadLocalization();
                plugin.loadModules();
                player.sendMessage(plugin.getMsg("plugin_reloaded"));
                return true;
            }
        }

        menuManager.openMenu(player, claim);
        return true;
    }

    private void sendHelp(Player player) {
        player.sendMessage(plugin.getMsg("help_header"));
        player.sendMessage(plugin.getMsg("help_gui"));
        player.sendMessage(plugin.getMsg("help_footer"));
    }
}
