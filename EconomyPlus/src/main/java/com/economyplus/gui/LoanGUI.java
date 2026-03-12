package com.economyplus.gui;

import com.economyplus.EconomyPlus;
import com.economyplus.loan.Loan;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LoanGUI {

    public static void openLoanMenu(EconomyPlus plugin, Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, ChatColor.DARK_RED + "Loan Manager");
        ItemStack filler = BankGUI.makeItem(Material.RED_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < 54; i++) inv.setItem(i, filler);

        double loanRate = plugin.getBankManager().getEffectiveLoanRate();
        double maxLoan = plugin.getBankManager().getMaxLoanAmount(player.getUniqueId());

        inv.setItem(4, BankGUI.makeItem(Material.PAPER,
            ChatColor.RED + "Loan System",
            Arrays.asList(
                ChatColor.WHITE + "Interest Rate: " + ChatColor.YELLOW + String.format("%.2f%%", loanRate * 100),
                ChatColor.WHITE + "Max Loan: " + ChatColor.GREEN + plugin.getEconomyManager().format(maxLoan),
                ChatColor.WHITE + "Repayment: " + ChatColor.AQUA + plugin.getConfigManager().getLoanRepaymentDays() + " days"
            )));

        // Loan amounts
        double[] amounts = {500, 1000, 5000, 10000, 50000, 100000};
        Material[] mats = {Material.IRON_INGOT, Material.GOLD_INGOT, Material.EMERALD, Material.DIAMOND, Material.NETHERITE_INGOT, Material.NETHER_STAR};
        int[] slots = {19, 20, 21, 23, 24, 25};

        for (int i = 0; i < amounts.length; i++) {
            double amt = amounts[i];
            double interest = amt * loanRate;
            inv.setItem(slots[i], BankGUI.makeItem(mats[i],
                ChatColor.YELLOW + "Take Loan: " + plugin.getEconomyManager().format(amt),
                Arrays.asList(
                    ChatColor.GRAY + "Total to repay: " + ChatColor.RED + plugin.getEconomyManager().format(amt + interest),
                    ChatColor.GRAY + "Due in: " + ChatColor.WHITE + plugin.getConfigManager().getLoanRepaymentDays() + " days"
                )));
        }

        // Show active loans
        List<Loan> loans = plugin.getLoanManager().getLoans(player.getUniqueId());
        for (int i = 0; i < Math.min(loans.size(), 5); i++) {
            Loan loan = loans.get(i);
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.WHITE + "Remaining: " + ChatColor.RED + plugin.getEconomyManager().format(loan.getRemaining()));
            lore.add(ChatColor.WHITE + "Due in: " + (loan.isOverdue() ? ChatColor.RED + "OVERDUE!" : ChatColor.YELLOW + loan.getTimeLeft()));
            lore.add(ChatColor.WHITE + "Rate: " + ChatColor.YELLOW + String.format("%.2f%%", loan.getInterestRate() * 100));
            lore.add("");
            lore.add(ChatColor.GREEN + "Click to repay " + plugin.getEconomyManager().format(Math.min(500, loan.getRemaining())));

            inv.setItem(37 + i, BankGUI.makeItem(
                loan.isOverdue() ? Material.RED_CONCRETE : Material.ORANGE_CONCRETE,
                ChatColor.GOLD + "Active Loan #" + (i + 1), lore));
        }

        if (loans.isEmpty()) {
            inv.setItem(40, BankGUI.makeItem(Material.LIME_CONCRETE,
                ChatColor.GREEN + "No Active Loans!", List.of(ChatColor.GRAY + "You're debt free!")));
        }

        inv.setItem(49, BankGUI.makeItem(Material.ARROW, ChatColor.RED + "← Back to Bank"));
        player.openInventory(inv);
    }

    public static void handleClick(EconomyPlus plugin, Player player, int slot) {
        double[] amounts = {500, 1000, 5000, 10000, 50000, 100000};
        int[] slots = {19, 20, 21, 23, 24, 25};
        String prefix = plugin.getConfigManager().getPrefix();

        for (int i = 0; i < slots.length; i++) {
            if (slot == slots[i]) {
                String error = plugin.getLoanManager().takeLoan(player.getUniqueId(), amounts[i]);
                if (error == null) {
                    player.sendMessage(prefix + ChatColor.GREEN + "Loan of "
                        + plugin.getEconomyManager().format(amounts[i]) + " approved!");
                    plugin.getScoreboardManager().updateScoreboard(player);
                    openLoanMenu(plugin, player);
                } else {
                    player.sendMessage(prefix + ChatColor.RED + error);
                }
                return;
            }
        }

        // Loan repayment slots (37-41)
        if (slot >= 37 && slot <= 41) {
            int idx = slot - 37;
            List<Loan> loans = plugin.getLoanManager().getLoans(player.getUniqueId());
            if (idx < loans.size()) {
                double repayAmt = Math.min(500, loans.get(idx).getRemaining());
                String error = plugin.getLoanManager().repayLoan(player.getUniqueId(), idx, repayAmt);
                if (error == null) {
                    player.sendMessage(prefix + ChatColor.GREEN + "Repaid " + plugin.getEconomyManager().format(repayAmt));
                    plugin.getScoreboardManager().updateScoreboard(player);
                    openLoanMenu(plugin, player);
                } else {
                    player.sendMessage(prefix + ChatColor.RED + error);
                }
            }
        }

        if (slot == 49) BankGUI.openMainMenu(plugin, player);
    }
}
