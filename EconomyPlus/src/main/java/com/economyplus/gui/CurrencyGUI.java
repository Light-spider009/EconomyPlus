package com.economyplus.gui;

import com.economyplus.EconomyPlus;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class CurrencyGUI {

    public static void openCurrencyMenu(EconomyPlus plugin, Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, ChatColor.GOLD + "Withdraw Currency");
        ItemStack filler = BankGUI.makeItem(Material.YELLOW_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < 27; i++) inv.setItem(i, filler);

        double balance = plugin.getEconomyManager().getBalance(player.getUniqueId());
        String sym = plugin.getConfigManager().getCurrencySymbol();

        inv.setItem(4, BankGUI.makeItem(Material.GOLD_BLOCK,
            ChatColor.GOLD + "Physical Currency Notes",
            Arrays.asList(
                ChatColor.WHITE + "Wallet: " + ChatColor.GREEN + plugin.getEconomyManager().format(balance),
                ChatColor.GRAY + "Right-click a note to deposit it",
                ChatColor.GRAY + "Drop notes to give to players"
            )));

        List<Integer> denoms = plugin.getConfigManager().getDenominations();
        Material[] mats = {Material.PAPER, Material.MAP, Material.FILLED_MAP,
            Material.BOOK, Material.WRITABLE_BOOK, Material.WRITTEN_BOOK, Material.ENCHANTED_BOOK};
        int[] slots = {10, 11, 12, 13, 14, 15, 16};

        for (int i = 0; i < Math.min(denoms.size(), slots.length); i++) {
            int denom = denoms.get(i);
            inv.setItem(slots[i], BankGUI.makeItem(mats[i % mats.length],
                ChatColor.GOLD + "" + ChatColor.BOLD + sym + denom + " Note",
                Arrays.asList(
                    ChatColor.YELLOW + "Value: " + ChatColor.GREEN + sym + String.format("%,d", denom),
                    ChatColor.GRAY + "Click to withdraw this note",
                    ChatColor.DARK_GRAY + "Physical tradeable currency"
                )));
        }

        inv.setItem(22, BankGUI.makeItem(Material.ARROW, ChatColor.RED + "← Back to Bank"));
        player.openInventory(inv);
    }

    public static void handleClick(EconomyPlus plugin, Player player, int slot) {
        List<Integer> denoms = plugin.getConfigManager().getDenominations();
        int[] slots = {10, 11, 12, 13, 14, 15, 16};
        String prefix = plugin.getConfigManager().getPrefix();

        for (int i = 0; i < Math.min(denoms.size(), slots.length); i++) {
            if (slot == slots[i]) {
                String error = plugin.getCurrencyManager().withdrawCurrency(player, denoms.get(i));
                if (error == null) {
                    player.sendMessage(prefix + ChatColor.GREEN + "Withdrew "
                        + plugin.getConfigManager().getCurrencySymbol() + denoms.get(i) + " note!");
                    player.closeInventory();
                } else {
                    player.sendMessage(prefix + ChatColor.RED + error);
                }
                return;
            }
        }
        if (slot == 22) BankGUI.openMainMenu(plugin, player);
    }
}
