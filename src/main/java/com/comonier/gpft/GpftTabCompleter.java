package com.comonier.gpft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GpftTabCompleter implements TabCompleter {
   @Nullable
   @Override
   public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
      List<String> suggestions = new ArrayList<>();
      
      // Primeiro argumento: Sub-comandos principais
      if (args.length == 1) {
         String input = args[0].toLowerCase();
         List<String> options = new ArrayList<>(Arrays.asList("help", "action", "screen", "chat", "boss"));
         
         // Apenas sugere reload para administradores
         if (sender.hasPermission("gpft.admin")) {
            options.add("reload");
         }
         
         for (String s : options) {
            if (s.startsWith(input)) {
               suggestions.add(s);
            }
         }
      } 
      // Segundo argumento: Estados (Liga/Desliga/On/Off etc)
      else if (args.length == 2) {
         String sub = args[0].toLowerCase();
         // Só sugere estados se o primeiro argumento for um dos tipos de notificação
         if (Arrays.asList("action", "screen", "chat", "boss").contains(sub)) {
            String input = args[1].toLowerCase();
            List<String> states = Arrays.asList("liga", "desliga", "on", "off", "si", "no", "true", "false");
            
            for (String s : states) {
               if (s.startsWith(input)) {
                  suggestions.add(s);
               }
            }
         }
      }

      return suggestions;
   }
}
