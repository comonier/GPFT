package com.comonier.gpft;

import me.ryanhamshire.GriefPrevention.Claim;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
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
      // Sub-comando Reload
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
      }

      // Verificação de Jogador
      if (!(sender instanceof Player)) {
         sender.sendMessage(this.plugin.getMsg("only_players"));
         return true;
      }

      Player player = (Player) sender;

      // Sistema de Toggle de Notificações (Action, Screen, Chat, Boss)
      if (args.length >= 2) {
         String sub = args[0].toLowerCase();
         if (sub.equals("action") || sub.equals("screen") || sub.equals("chat") || sub.equals("boss")) {
            String val = args[1].toLowerCase();
            
            // Aceita múltiplos termos para facilitar (liga/on/si/да ou desliga/off/no/нет)
            boolean enable = val.equals("liga") || val.equals("on") || val.equals("si") || val.equals("да") || val.equals("true");
            
            NamespacedKey key = new NamespacedKey(plugin, "hide_notify_" + sub);
            if (enable) {
               // Se ligar, removemos a marcação de "escondido"
               player.getPersistentDataContainer().remove(key);
            } else {
               // Se desligar, marcamos como escondido
               player.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);
            }

            // Busca os termos Ligado/Desligado traduzidos do arquivo YAML correspondente
            String statusStr = enable ? plugin.getMsgRaw("status_enabled") : plugin.getMsgRaw("status_disabled");
            
            player.sendMessage(plugin.getMsg("msg_notification_toggle")
                    .replace("{type}", sub.toUpperCase())
                    .replace("{status}", statusStr));
            return true;
         }
      }

      // Sub-comando Help
      if (args.length >= 1 && args[0].equalsIgnoreCase("help")) {
         this.sendHelp(player);
         return true;
      } 
      
      // Comando Principal (Abre GUI)
      else {
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

   private void sendHelp(Player player) {
      player.sendMessage(this.plugin.getMsg("help_header"));
      player.sendMessage(this.plugin.getMsg("help_gui"));
      player.sendMessage(this.plugin.getMsg("help_toggle"));
      player.sendMessage(this.plugin.getMsg("help_reload"));
      player.sendMessage(this.plugin.getMsg("help_footer"));
   }
}
