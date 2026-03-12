package com.economyplus.gui;

import com.economyplus.EconomyPlus;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class BankGUI {

    public static void openMainMenu(EconomyPlus plugin, Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, ChatColor.DARK_BLUE + "EconomyPlus Bank | Main Menu");
        double balance = plugin.getEconomyManager().getBalance(player.getUniqueId());
        double deposit = plugin.getBankManager().getDeposit(player.getUniqueId());
        double depositRate = plugin.getBankManager().getEffectiveDepositRate();
        double loanRate = plugin.getBankManager().getEffectiveLoanRate();

        // Fill border
        ItemStack filler = makeItem(Material.BLUE_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < 27; i++) inv.setItem(i, filler);

        // Balance info
        inv.setItem(4, makeItem(Material.GOLD_INGOT,
            ChatColor.GOLD + "Your Finances",
            Arrays.asList(
                ChatColor.WHITE + "Wallet: " + ChatColor.GREEN + plugin.getEconomyManager().format(balance),
                ChatColor.WHITE + "Bank Deposit: " + ChatColor.AQUA + plugin.getEconomyManager().format(deposit),
                ChatColor.WHITE + "Deposit Rate: " + ChatColor.YELLOW + String.format("%.2f%%", depositRate * 100),
                ChatColor.WHITE + "Loan Rate: " + ChatColor.RED + String.format("%.2f%%", loanRate * 100)
            )));

        // Deposit button
        inv.setItem(10, makeItem(Material.CHEST,
            ChatColor.GREEN + "💰 Deposit Money",
            Arrays.asList(ChatColor.GRAY + "Deposit money into the bank",
                ChatColor.YELLOW + "Earn " + String.format("%.2f%%", depositRate * 100) + " interest")));

        // Withdraw button
        inv.setItem(12, makeItem(Material.ENDER_CHEST,
            ChatColor.AQUA + "💸 Withdraw Money",
            Arrays.asList(ChatColor.GRAY + "Withdraw from your bank account")));

        // Loan button
        inv.setItem(14, makeItem(Material.PAPER,
            ChatColor.RED + "📄 Loan Manager",
            Arrays.asList(ChatColor.GRAY + "Take or manage loans",
                ChatColor.YELLOW + "Rate: " + String.format("%.2f%%", loanRate * 100))));

        // Currency button
        inv.setItem(16, makeItem(Material.MAP,
            ChatColor.YELLOW + "💵 Physical Currency",
            Arrays.asList(ChatColor.GRAY + "Withdraw physical bank notes",
                ChatColor.GRAY + "Trade with other players!")));

        // GDP info
        double inflation = plugin.getEconomyManager().getCurrentInflationRate();
        inv.setItem(22, makeItem(Material.COMPASS,
            ChatColor.GOLD + "📊 Server Economy",
            Arrays.asList(
                ChatColor.WHITE + "GDP: " + ChatColor.GREEN + plugin.getEconomyManager().format(plugin.getEconomyManager().getServerGDP()),
                ChatColor.WHITE + "Inflation: " + (inflation >= 0 ? ChatColor.RED : ChatColor.GREEN)
                    + String.format("%.2f%%", inflation * 100),
                ChatColor.WHITE + "Money Supply: " + ChatColor.YELLOW + plugin.getEconomyManager().format(plugin.getEconomyManager().getTotalMoneySupply())
            )));

        player.openInventory(inv);
    }

    public static void openDepositMenu(EconomyPlus plugin, Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, ChatColor.DARK_BLUE + "EconomyPlus Bank | Deposit");
        ItemStack filler = makeItem(Material.BLUE_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < 27; i++) inv.setItem(i, filler);

        double balance = plugin.getEconomyManager().getBalance(player.getUniqueId());
        inv.setItem(4, makeItem(Material.GOLD_INGOT,
            ChatColor.GREEN + "Deposit Money",
            List.of(ChatColor.WHITE + "Wallet: " + ChatColor.GREEN + plugin.getEconomyManager().format(balance))));

        double[] amounts = {100, 500, 1000, 5000, 10000, 50000, 100000};
        Material[] mats = {Material.IRON_NUGGET, Material.IRON_INGOT, Material.GOLD_NUGGET,
            Material.GOLD_INGOT, Material.EMERALD, Material.DIAMOND, Material.NETHERITE_INGOT};
        int[] slots = {10, 11, 12, 13, 14, 15, 16};

        for (int i = 0; i < amounts.length; i++) {
            double amt = amounts[i];
            inv.setItem(slots[i], makeItem(mats[i],
                ChatColor.GREEN + "Deposit " + plugin.getEconomyManager().format(amt),
                List.of(ChatColor.GRAY + "Click to deposit this amount")));
        }

        // Deposit all
        inv.setItem(22, makeItem(Material.EMERALD_BLOCK,
            ChatColor.GOLD + "Deposit ALL",
            List.of(ChatColor.GRAY + "Deposit your entire wallet")));

        // Back
        inv.setItem(26, makeItem(Material.ARROW, ChatColor.RED + "← Back"));
        player.openInventory(inv);
    }

    public static void openWithdrawMenu(EconomyPlus plugin, Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, ChatColor.DARK_BLUE + "EconomyPlus Bank | Withdraw");
        ItemStack filler = makeItem(Material.BLUE_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < 27; i++) inv.setItem(i, filler);

        double deposit = plugin.getBankManager().getDeposit(player.getUniqueId());
        inv.setItem(4, makeItem(Material.ENDER_CHEST,
            ChatColor.AQUA + "Withdraw Money",
            List.of(ChatColor.WHITE + "Bank Balance: " + ChatColor.AQUA + plugin.getEconomyManager().format(deposit))));

        double[] amounts = {100, 500, 1000, 5000, 10000, 50000, 100000};
        Material[] mats = {Material.IRON_NUGGET, Material.IRON_INGOT, Material.GOLD_NUGGET,
            Material.GOLD_INGOT, Material.EMERALD, Material.DIAMOND, Material.NETHERITE_INGOT};
        int[] slots = {10, 11, 12, 13, 14, 15, 16};

        for (int i = 0; i < amounts.length; i++) {
            double amt = amounts[i];
            inv.setItem(slots[i], makeItem(mats[i],
                ChatColor.AQUA + "Withdraw " + plugin.getEconomyManager().format(amt),
                List.of(ChatColor.GRAY + "Click to withdraw this amount")));
        }

        inv.setItem(22, makeItem(Material.DIAMOND_BLOCK,
            ChatColor.GOLD + "Withdraw ALL",
            List.of(ChatColor.GRAY + "Withdraw your entire bank balance")));

        inv.setItem(26, makeItem(Material.ARROW, ChatColor.RED + "← Back"));
        player.openInventory(inv);
    }

    public static void handleMainMenuClick(EconomyPlus plugin, Player player, int slot) {
        switch (slot) {
            case 10 -> openDepositMenu(plugin, player);
            case 12 -> openWithdrawMenu(plugin, player);
            case 14 -> LoanGUI.openLoanMenu(plugin, player);
            case 16 -> CurrencyGUI.openCurrencyMenu(plugin, player);
        }
    }

    public static void handleDepositClick(EconomyPlus plugin, Player player, int slot) {
        double[] amounts = {100, 500, 1000, 5000, 10000, 50000, 100000};
        int[] slots = {10, 11, 12, 13, 14, 15, 16};
        String prefix = plugin.getConfigManager().getPrefix();

        for (int i = 0; i < slots.length; i++) {
            if (slot == slots[i]) {
                if (plugin.getBankManager().depositToBank(player.getUniqueId(), amounts[i])) {
                    player.sendMessage(prefix + ChatColor.GREEN + "Deposited " + plugin.getEconomyManager().format(amounts[i]));
                    plugin.getScoreboardManager().updateScoreboard(player);
                    openDepositMenu(plugin, player);
                } else {
                    player.sendMessage(prefix + ChatColor.RED + "Insufficient funds or deposit limit reached.");
                }
                return;
            }
        }
        if (slot == 22) { // Deposit all
            double all = plugin.getEconomyManager().getBalance(player.getUniqueId());
            if (plugin.getBankManager().depositToBank(player.getUniqueId(), all)) {
                player.sendMessage(prefix + ChatColor.GREEN + "Deposited all: " + plugin.getEconomyManager().format(all));
                plugin.getScoreboardManager().updateScoreboard(player);
                openDepositMenu(plugin, player);
            } else {
                player.sendMessage(prefix + ChatColor.RED + "Could not deposit all funds.");
            }
        } else if (slot == 26) {
            openMainMenu(plugin, player);
        }
    }

    public static void handleWithdrawClick(EconomyPlus plugin, Player player, int slot) {
        double[] amounts = {100, 500, 1000, 5000, 10000, 50000, 100000};
        int[] slots = {10, 11, 12, 13, 14, 15, 16};
        String prefix = plugin.getConfigManager().getPrefix();

        for (int i = 0; i < slots.length; i++) {
            if (slot == slots[i]) {
                if (plugin.getBankManager().withdrawFromBank(player.getUniqueId(), amounts[i])) {
                    player.sendMessage(prefix + ChatColor.GREEN + "Withdrew " + plugin.getEconomyManager().format(amounts[i]));
                    plugin.getScoreboardManager().updateScoreboard(player);
                    openWithdrawMenu(plugin, player);
                } else {
                    player.sendMessage(prefix + ChatColor.RED + "Insufficient bank balance.");
                }
                return;
            }
        }
        if (slot == 22) { // Withdraw all
            double all = plugin.getBankManager().getDeposit(player.getUniqueId());
            if (plugin.getBankManager().withdrawFromBank(player.getUniqueId(), all)) {
                player.sendMessage(prefix + ChatColor.GREEN + "Withdrew all: " + plugin.getEconomyManager().format(all));
                plugin.getScoreboardManager().updateScoreboard(player);
                openWithdrawMenu(plugin, player);
            } else {
                player.sendMessage(prefix + ChatColor.RED + "Nothing to withdraw.");
            }
        } else if (slot == 26) {
            openMainMenu(plugin, player);
        }
    }

    static ItemStack makeItem(Material mat, String name, List<String> lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        if (lore != null) meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    static ItemStack makeItem(Material mat, String name) {
        return makeItem(mat, name, null);
    }
}
