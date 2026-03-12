package com.economyplus.commands;

import com.economyplus.EconomyPlus;
import com.economyplus.gui.LoanGUI;
import com.economyplus.loan.Loan;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class LoanCommand implements CommandExecutor {
    private final EconomyPlus plugin;
    public LoanCommand(EconomyPlus plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        String prefix = plugin.getConfigManager().getPrefix();
        if (!(sender instanceof Player player)) { sender.sendMessage("Only players."); return true; }
        if (!player.hasPermission("economyplus.loan")) { player.sendMessage(plugin.getConfigManager().getNoPermission()); return true; }

        if (args.length == 0) { LoanGUI.openLoanMenu(plugin, player); return true; }

        switch (args[0].toLowerCase()) {
            case "take" -> {
                if (args.length < 2) { player.sendMessage(prefix + ChatColor.RED + "Usage: /loan take <amount>"); return true; }
                try {
                    double amount = Double.parseDouble(args[1]);
                    String error = plugin.getLoanManager().takeLoan(player.getUniqueId(), amount);
                    if (error == null) player.sendMessage(prefix + ChatColor.GREEN + "Loan of " + plugin.getEconomyManager().format(amount) + " approved!");
                    else player.sendMessage(prefix + ChatColor.RED + error);
                } catch (NumberFormatException e) { player.sendMessage(prefix + ChatColor.RED + "Invalid amount."); }
            }
            case "repay" -> {
                if (args.length < 2) { player.sendMessage(prefix + ChatColor.RED + "Usage: /loan repay <amount>"); return true; }
                try {
                    double amount = Double.parseDouble(args[1]);
                    List<Loan> loans = plugin.getLoanManager().getLoans(player.getUniqueId());
                    if (loans.isEmpty()) { player.sendMessage(prefix + ChatColor.YELLOW + "No active loans."); return true; }
                    String error = plugin.getLoanManager().repayLoan(player.getUniqueId(), 0, amount);
                    if (error == null) player.sendMessage(prefix + ChatColor.GREEN + "Repaid " + plugin.getEconomyManager().format(amount));
                    else player.sendMessage(prefix + ChatColor.RED + error);
                } catch (NumberFormatException e) { player.sendMessage(prefix + ChatColor.RED + "Invalid amount."); }
            }
            case "info" -> {
                List<Loan> loans = plugin.getLoanManager().getLoans(player.getUniqueId());
                if (loans.isEmpty()) { player.sendMessage(prefix + ChatColor.GREEN + "No active loans!"); return true; }
                player.sendMessage(prefix + ChatColor.YELLOW + "Active Loans:");
                for (int i = 0; i < loans.size(); i++) {
                    Loan l = loans.get(i);
                    player.sendMessage(ChatColor.GRAY + " #" + (i+1) + ": " + ChatColor.RED
                        + plugin.getEconomyManager().format(l.getRemaining())
                        + ChatColor.GRAY + " | Due: " + (l.isOverdue() ? ChatColor.RED + "OVERDUE" : ChatColor.YELLOW + l.getTimeLeft()));
                }
            }
            default -> LoanGUI.openLoanMenu(plugin, player);
        }
        return true;
    }
}
