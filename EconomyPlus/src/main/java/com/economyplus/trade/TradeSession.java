package com.economyplus.trade;

import com.economyplus.EconomyPlus;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.UUID;

public class TradeSession {

    private final EconomyPlus plugin;
    private final Player player1;
    private final Player player2;
    private boolean p1Confirmed = false;
    private boolean p2Confirmed = false;
    private boolean cancelled = false;
    private Inventory inv1, inv2;

    // Trade slots: 0-3 for player1 offer, 5-8 for player2 offer
    public static final int[] OFFER_SLOTS_P1 = {0, 1, 2, 3};
    public static final int[] OFFER_SLOTS_P2 = {5, 6, 7, 8};
    public static final int CONFIRM_SLOT = 19;
    public static final int CANCEL_SLOT = 25;
    public static final int DIVIDER = 4;

    public TradeSession(EconomyPlus plugin, Player player1, Player player2) {
        this.plugin = plugin;
        this.player1 = player1;
        this.player2 = player2;
    }

    public void openForBoth() {
        inv1 = buildGUI(player1, player2);
        inv2 = buildGUI(player2, player1);
        player1.openInventory(inv1);
        player2.openInventory(inv2);
    }

    private Inventory buildGUI(Player viewer, Player other) {
        Inventory inv = Bukkit.createInventory(null, 36,
            ChatColor.DARK_GREEN + "Trade: " + viewer.getName() + " ↔ " + other.getName());

        // Fill with glass dividers
        ItemStack glass = makeItem(Material.GRAY_STAINED_GLASS_PANE, ChatColor.DARK_GRAY + "─────────────");
        for (int i = 9; i < 36; i++) inv.setItem(i, glass);

        // Confirm button
        inv.setItem(CONFIRM_SLOT, makeItem(Material.LIME_WOOL,
            ChatColor.GREEN + "" + ChatColor.BOLD + "✔ CONFIRM TRADE",
            Arrays.asList(ChatColor.GRAY + "Click to confirm your offer",
                ChatColor.YELLOW + "Both players must confirm!")));

        // Cancel button
        inv.setItem(CANCEL_SLOT, makeItem(Material.RED_WOOL,
            ChatColor.RED + "" + ChatColor.BOLD + "✘ CANCEL TRADE",
            Arrays.asList(ChatColor.GRAY + "Cancels the trade")));

        // Labels
        inv.setItem(9, makeItem(Material.PLAYER_HEAD,
            ChatColor.AQUA + "" + ChatColor.BOLD + viewer.getName() + "'s Offer"));
        inv.setItem(14, makeItem(Material.PLAYER_HEAD,
            ChatColor.GOLD + "" + ChatColor.BOLD + other.getName() + "'s Offer"));

        return inv;
    }

    public void confirm(Player player) {
        if (player.getUniqueId().equals(player1.getUniqueId())) {
            p1Confirmed = true;
            player2.sendMessage(plugin.getConfigManager().getPrefix()
                + plugin.getConfigManager().colorize("&a" + player1.getName() + " confirmed the trade!"));
        } else {
            p2Confirmed = true;
            player1.sendMessage(plugin.getConfigManager().getPrefix()
                + plugin.getConfigManager().colorize("&a" + player2.getName() + " confirmed the trade!"));
        }
        updateConfirmButtons();
        if (p1Confirmed && p2Confirmed) executeTrade();
    }

    private void updateConfirmButtons() {
        // Update button colors to show confirmed state
        if (inv1 != null) {
            Material mat = p1Confirmed ? Material.GREEN_CONCRETE : Material.LIME_WOOL;
            inv1.setItem(CONFIRM_SLOT, makeItem(mat,
                p1Confirmed ? ChatColor.GREEN + "✔ Confirmed!" : ChatColor.GREEN + "✔ CONFIRM TRADE"));
        }
        if (inv2 != null) {
            Material mat = p2Confirmed ? Material.GREEN_CONCRETE : Material.LIME_WOOL;
            inv2.setItem(CONFIRM_SLOT, makeItem(mat,
                p2Confirmed ? ChatColor.GREEN + "✔ Confirmed!" : ChatColor.GREEN + "✔ CONFIRM TRADE"));
        }
    }

    private void executeTrade() {
        cancelled = true;
        // Collect items from each side
        ItemStack[] p1Items = getSlotsFromInv(inv1, OFFER_SLOTS_P1);
        ItemStack[] p2Items = getSlotsFromInv(inv2, OFFER_SLOTS_P2);

        player1.closeInventory();
        player2.closeInventory();

        // Give items to other player
        for (ItemStack item : p1Items) {
            if (item != null && item.getType() != Material.AIR) {
                player2.getInventory().addItem(item);
            }
        }
        for (ItemStack item : p2Items) {
            if (item != null && item.getType() != Material.AIR) {
                player1.getInventory().addItem(item);
            }
        }

        plugin.getConfigManager();
        String prefix = plugin.getConfigManager().getPrefix();
        player1.sendMessage(prefix + ChatColor.GREEN + "Trade completed successfully!");
        player2.sendMessage(prefix + ChatColor.GREEN + "Trade completed successfully!");

        plugin.getTradeManager().closeSession(player1.getUniqueId());
    }

    public void cancel(Player canceller) {
        if (cancelled) return;
        cancelled = true;
        // Return items to owners
        returnItems(inv1, OFFER_SLOTS_P1, player1);
        returnItems(inv2, OFFER_SLOTS_P2, player2);
        player1.closeInventory();
        player2.closeInventory();
        String prefix = plugin.getConfigManager().getPrefix();
        player1.sendMessage(prefix + ChatColor.RED + "Trade cancelled by " + canceller.getName() + ".");
        player2.sendMessage(prefix + ChatColor.RED + "Trade cancelled by " + canceller.getName() + ".");
        plugin.getTradeManager().closeSession(player1.getUniqueId());
    }

    private void returnItems(Inventory inv, int[] slots, Player player) {
        if (inv == null) return;
        for (int slot : slots) {
            ItemStack item = inv.getItem(slot);
            if (item != null && item.getType() != Material.AIR) {
                player.getInventory().addItem(item);
            }
        }
    }

    private ItemStack[] getSlotsFromInv(Inventory inv, int[] slots) {
        ItemStack[] items = new ItemStack[slots.length];
        for (int i = 0; i < slots.length; i++) {
            items[i] = inv.getItem(slots[i]);
        }
        return items;
    }

    private ItemStack makeItem(Material mat, String name, java.util.List<String> lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        if (lore != null) meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack makeItem(Material mat, String name) {
        return makeItem(mat, name, null);
    }

    public UUID getOtherUUID(UUID uuid) {
        return uuid.equals(player1.getUniqueId()) ? player2.getUniqueId() : player1.getUniqueId();
    }

    public boolean isCancelled() { return cancelled; }
    public Inventory getInv1() { return inv1; }
    public Inventory getInv2() { return inv2; }
    public Player getPlayer1() { return player1; }
    public Player getPlayer2() { return player2; }
    public boolean isP1Confirmed() { return p1Confirmed; }
    public boolean isP2Confirmed() { return p2Confirmed; }
    public void resetConfirmation() { p1Confirmed = false; p2Confirmed = false; }
}
