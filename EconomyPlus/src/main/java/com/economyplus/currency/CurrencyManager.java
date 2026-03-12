package com.economyplus.currency;

import com.economyplus.EconomyPlus;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class CurrencyManager {

    private final EconomyPlus plugin;
    public static final String CURRENCY_PDC_KEY = "economyplus_currency_value";

    public CurrencyManager(EconomyPlus plugin) {
        this.plugin = plugin;
    }

    /**
     * Creates a physical paper currency item with the given denomination.
     */
    public ItemStack createCurrencyNote(int denomination) {
        ItemStack note = new ItemStack(Material.PAPER);
        ItemMeta meta = note.getItemMeta();
        String symbol = plugin.getConfigManager().getCurrencySymbol();
        String name = plugin.getConfigManager().getCurrencyName();

        meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + symbol + denomination + " " + name + " Note");
        meta.setLore(Arrays.asList(
            ChatColor.YELLOW + "Value: " + ChatColor.GREEN + symbol + String.format("%,d", denomination),
            ChatColor.GRAY + "Right-click to deposit to wallet",
            ChatColor.GRAY + "Drop to give to another player",
            ChatColor.DARK_GRAY + "Issued by the EconomyPlus Bank",
            ChatColor.DARK_GRAY + "Serial: " + UUID.randomUUID().toString().substring(0, 8).toUpperCase()
        ));
        meta.setCustomModelData(denomination);

        // Store value in PDC for anti-duplication
        NamespacedKey key = new NamespacedKey(plugin, CURRENCY_PDC_KEY);
        meta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, denomination);

        note.setItemMeta(meta);
        return note;
    }

    /**
     * Returns the currency value of an ItemStack, or -1 if not currency.
     */
    public int getCurrencyValue(ItemStack item) {
        if (item == null || item.getType() != Material.PAPER || !item.hasItemMeta()) return -1;
        NamespacedKey key = new NamespacedKey(plugin, CURRENCY_PDC_KEY);
        var container = item.getItemMeta().getPersistentDataContainer();
        if (!container.has(key, PersistentDataType.INTEGER)) return -1;
        return container.get(key, PersistentDataType.INTEGER);
    }

    public boolean isCurrencyNote(ItemStack item) {
        return getCurrencyValue(item) > 0;
    }

    /**
     * Give physical currency notes to a player by withdrawing from their balance.
     */
    public String withdrawCurrency(Player player, int denomination) {
        List<Integer> denominations = plugin.getConfigManager().getDenominations();
        if (!denominations.contains(denomination)) {
            return "Invalid denomination. Valid: " + denominations.toString();
        }
        if (!plugin.getEconomyManager().withdraw(player.getUniqueId(), denomination)) {
            return "Insufficient funds.";
        }
        ItemStack note = createCurrencyNote(denomination);
        player.getInventory().addItem(note);
        return null; // success
    }

    /**
     * Deposit a physical currency note back to wallet.
     */
    public boolean depositCurrencyNote(Player player, ItemStack item) {
        int value = getCurrencyValue(item);
        if (value <= 0) return false;
        item.setAmount(item.getAmount() - 1);
        plugin.getEconomyManager().deposit(player.getUniqueId(), value);
        player.sendMessage(plugin.getConfigManager().getPrefix()
            + plugin.getConfigManager().colorize("&aDeposited &e" + plugin.getEconomyManager().format(value) + " &afrom note to wallet."));
        return true;
    }
}
