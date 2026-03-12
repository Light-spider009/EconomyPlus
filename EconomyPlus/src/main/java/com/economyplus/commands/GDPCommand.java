package com.economyplus.commands;

import com.economyplus.EconomyPlus;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GDPCommand implements CommandExecutor {
    private final EconomyPlus plugin;
    public GDPCommand(EconomyPlus plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        String prefix = plugin.getConfigManager().getPrefix();
        double gdp = plugin.getEconomyManager().getServerGDP();
        double inflation = plugin.getEconomyManager().getCurrentInflationRate();
        double supply = plugin.getEconomyManager().getTotalMoneySupply();
        double deposits = plugin.getBankManager().getTotalDeposits();
        int players = plugin.getServer().getOnlinePlayers().size();

        sender.sendMessage(prefix + ChatColor.GOLD + "=== Server Economy Report ===");
        sender.sendMessage(ChatColor.WHITE + "Server GDP: " + ChatColor.GREEN + plugin.getEconomyManager().format(gdp));
        sender.sendMessage(ChatColor.WHITE + "Money Supply: " + ChatColor.YELLOW + plugin.getEconomyManager().format(supply));
        sender.sendMessage(ChatColor.WHITE + "Total Bank Deposits: " + ChatColor.AQUA + plugin.getEconomyManager().format(deposits));
        sender.sendMessage(ChatColor.WHITE + "Inflation Rate: "
            + (inflation >= 0 ? ChatColor.RED : ChatColor.GREEN)
            + String.format("%.2f%%", inflation * 100));
        sender.sendMessage(ChatColor.WHITE + "Online Players: " + ChatColor.YELLOW + players);
        sender.sendMessage(ChatColor.WHITE + "Deposit Interest Rate: "
            + ChatColor.GREEN + String.format("%.2f%%", plugin.getBankManager().getEffectiveDepositRate() * 100));
        sender.sendMessage(ChatColor.WHITE + "Loan Interest Rate: "
            + ChatColor.RED + String.format("%.2f%%", plugin.getBankManager().getEffectiveLoanRate() * 100));
        return true;
    }
}
