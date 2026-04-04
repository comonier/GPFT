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

   public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
      if (args.length >= 1 && args[0].equalsIgnoreCase("reload")) {
         if (!sender.hasPermission("gpft.admin") && sender instanceof Player) {
            sender.sendMessage(this.plugin.getMsg("no_permission"));
         } else {
            this.plugin.reloadConfig();
            this.plugin.loadLocalization();
            this.plugin.loadModules();
            sender.sendMessage(this.plugin.getMsg("plugin_reloaded"));
         }

         return true;
      } else if (!(sender instanceof Player)) {
         sender.sendMessage(this.plugin.getMsg("only_players"));
         return true;
      } else {
         Player player = (Player)sender;
         if (args.length >= 1 && args[0].equalsIgnoreCase("help")) {
            this.sendHelp(player);
            return true;
         } else {
            Claim claim = this.flagManager.getClaimAt(player.getLocation());
            if (claim == null) {
               player.sendMessage(this.plugin.getMsg("not_in_claim"));
               return true;
            } else if (!this.flagManager.canManage(player, claim)) {
               player.sendMessage(this.plugin.getMsg("not_owner"));
               return true;
            } else {
               this.menuManager.openMenu(player, claim);
               return true;
            }
         }
      }
   }

   private void sendHelp(Player player) {
      player.sendMessage(this.plugin.getMsg("help_header"));
      player.sendMessage(this.plugin.getMsg("help_gui"));
      player.sendMessage(this.plugin.getMsg("help_reload"));
      player.sendMessage(this.plugin.getMsg("help_footer"));
   }
}
