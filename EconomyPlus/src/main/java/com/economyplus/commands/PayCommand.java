package com.economyplus.commands;

import com.economyplus.EconomyPlus;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PayCommand implements CommandExecutor {
    private final EconomyPlus plugin;
    public PayCommand(EconomyPlus plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String prefix = plugin.getConfigManager().getPrefix();
        if (!(sender instanceof Player player)) { sender.sendMessage("Only players."); return true; }
        if (!player.hasPermission("economyplus.pay")) { player.sendMessage(plugin.getConfigManager().getNoPermission()); return true; }
        if (args.length < 2) { player.sendMessage(prefix + ChatColor.RED + "Usage: /pay <player> <amount>"); return true; }

        Player target = plugin.getServer().getPlayer(args[0]);
        if (target == null) { player.sendMessage(plugin.getConfigManager().getPlayerNotFound()); return true; }
        if (target.equals(player)) { player.sendMessage(prefix + ChatColor.RED + "You can't pay yourself!"); return true; }

        double amount;
        try { amount = Double.parseDouble(args[1]); } catch (NumberFormatException e) {
            player.sendMessage(prefix + ChatColor.RED + "Invalid amount.");
            return true;
        }
        if (amount <= 0) { player.sendMessage(prefix + ChatColor.RED + "Amount must be positive."); return true; }

        if (!plugin.getEconomyManager().transfer(player.getUniqueId(), target.getUniqueId(), amount)) {
            player.sendMessage(prefix + ChatColor.RED + "Insufficient funds.");
            return true;
        }

        String fmt = plugin.getEconomyManager().format(amount);
        player.sendMessage(prefix + ChatColor.GREEN + "Paid " + ChatColor.GOLD + fmt + ChatColor.GREEN + " to " + target.getName());
        target.sendMessage(prefix + ChatColor.GREEN + "Received " + ChatColor.GOLD + fmt + ChatColor.GREEN + " from " + player.getName());
        plugin.getScoreboardManager().updateScoreboard(player);
        plugin.getScoreboardManager().updateScoreboard(target);
        return true;
    }
}
