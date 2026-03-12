package com.economyplus.commands;

import com.economyplus.EconomyPlus;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdminCommand implements CommandExecutor {
    private final EconomyPlus plugin;
    public AdminCommand(EconomyPlus plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        String prefix = plugin.getConfigManager().getPrefix();
        if (!sender.hasPermission("economyplus.admin")) { sender.sendMessage(plugin.getConfigManager().getNoPermission()); return true; }
        if (args.length < 3) {
            sender.sendMessage(prefix + ChatColor.RED + "Usage: /ecoadmin <give|take|set|reset> <player> <amount>");
            return true;
        }

        Player target = plugin.getServer().getPlayer(args[1]);
        if (target == null) { sender.sendMessage(plugin.getConfigManager().getPlayerNotFound()); return true; }

        if (args[0].equalsIgnoreCase("reset")) {
            plugin.getEconomyManager().setBalance(target.getUniqueId(), plugin.getConfigManager().getStartingBalance());
            sender.sendMessage(prefix + ChatColor.GREEN + "Reset " + target.getName() + "'s balance.");
            return true;
        }

        double amount;
        try { amount = Double.parseDouble(args[2]); } catch (NumberFormatException e) {
            sender.sendMessage(prefix + ChatColor.RED + "Invalid amount.");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "give" -> {
                plugin.getEconomyManager().deposit(target.getUniqueId(), amount);
                sender.sendMessage(prefix + ChatColor.GREEN + "Gave " + plugin.getEconomyManager().format(amount) + " to " + target.getName());
                target.sendMessage(prefix + ChatColor.GREEN + "An admin gave you " + plugin.getEconomyManager().format(amount));
            }
            case "take" -> {
                if (!plugin.getEconomyManager().withdraw(target.getUniqueId(), amount)) {
                    sender.sendMessage(prefix + ChatColor.RED + "Insufficient funds.");
                } else {
                    sender.sendMessage(prefix + ChatColor.GREEN + "Took " + plugin.getEconomyManager().format(amount) + " from " + target.getName());
                }
            }
            case "set" -> {
                plugin.getEconomyManager().setBalance(target.getUniqueId(), amount);
                sender.sendMessage(prefix + ChatColor.GREEN + "Set " + target.getName() + "'s balance to " + plugin.getEconomyManager().format(amount));
            }
            default -> sender.sendMessage(prefix + ChatColor.RED + "Unknown action: " + args[0]);
        }
        plugin.getScoreboardManager().updateScoreboard(target);
        return true;
    }
}
