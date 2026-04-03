package com.comonier.gpft;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/*
 * TabCompleter for GPFT
 * Provides suggestions for /gpft subcommands
 */
public class GpftTabCompleter implements TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> suggestions = new ArrayList<>();

        // Handle the first argument: /gpft [arg]
        if (args.length == 1) {
            String input = args[0].toLowerCase();
            
            // Suggest "help" to everyone
            if ("help".startsWith(input)) {
                suggestions.add("help");
            }
            
            // Suggest "reload" only if player has permission
            if (sender.hasPermission("gpft.admin")) {
                if ("reload".startsWith(input)) {
                    suggestions.add("reload");
                }
            }
        }

        // Return the list of suggestions
        return suggestions;
    }
}
