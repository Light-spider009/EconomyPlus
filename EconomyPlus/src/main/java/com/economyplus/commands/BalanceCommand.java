package com.economyplus.commands;

import com.economyplus.EconomyPlus;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BalanceCommand implements CommandExecutor {
    private final EconomyPlus plugin;
    public BalanceCommand(EconomyPlus plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String prefix = plugin.getConfigManager().getPrefix();
        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("Specify a player name.");
                return true;
            }
            double bal = plugin.getEconomyManager().getBalance(player.getUniqueId());
            double dep = plugin.getBankManager().getDeposit(player.getUniqueId());
            player.sendMessage(prefix + ChatColor.YELLOW + "Wallet: " + ChatColor.GREEN + plugin.getEconomyManager().format(bal));
            player.sendMessage(prefix + ChatColor.YELLOW + "Bank: " + ChatColor.AQUA + plugin.getEconomyManager().format(dep));
            player.sendMessage(prefix + ChatColor.YELLOW + "Total: " + ChatColor.GOLD + plugin.getEconomyManager().format(bal + dep));
        } else {
            Player target = plugin.getServer().getPlayer(args[0]);
            if (target == null) { sender.sendMessage(plugin.getConfigManager().getPlayerNotFound()); return true; }
            double bal = plugin.getEconomyManager().getBalance(target.getUniqueId());
            double dep = plugin.getBankManager().getDeposit(target.getUniqueId());
            sender.sendMessage(prefix + ChatColor.YELLOW + target.getName() + "'s Wallet: " + ChatColor.GREEN + plugin.getEconomyManager().format(bal));
            sender.sendMessage(prefix + ChatColor.YELLOW + target.getName() + "'s Bank: " + ChatColor.AQUA + plugin.getEconomyManager().format(dep));
        }
        return true;
    }
}
