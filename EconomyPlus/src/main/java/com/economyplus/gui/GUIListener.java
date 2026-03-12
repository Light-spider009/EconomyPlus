package com.economyplus.gui;

import com.economyplus.EconomyPlus;
import com.economyplus.trade.TradeSession;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public class GUIListener implements Listener {

    private final EconomyPlus plugin;

    public GUIListener(EconomyPlus plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        String title = event.getView().getTitle();

        // Bank GUI
        if (title.contains("EconomyPlus Bank")) {
            event.setCancelled(true);
            handleBankClick(player, event.getRawSlot(), title);
            return;
        }

        // Loan GUI
        if (title.contains("Loan Manager")) {
            event.setCancelled(true);
            handleLoanClick(player, event.getRawSlot());
            return;
        }

        // Currency withdraw GUI
        if (title.contains("Withdraw Currency")) {
            event.setCancelled(true);
            handleCurrencyClick(player, event.getRawSlot());
            return;
        }

        // Trade GUI
        if (title.contains("Trade: ")) {
            TradeSession session = plugin.getTradeManager().getSession(player.getUniqueId());
            if (session == null) return;
            int slot = event.getRawSlot();
            // Prevent placing in other player's offer area
            boolean isP1 = player.getUniqueId().equals(session.getPlayer1().getUniqueId());
            int[] ownSlots = isP1 ? TradeSession.OFFER_SLOTS_P1 : TradeSession.OFFER_SLOTS_P2;
            int[] otherSlots = isP1 ? TradeSession.OFFER_SLOTS_P2 : TradeSession.OFFER_SLOTS_P1;

            for (int s : otherSlots) {
                if (slot == s) { event.setCancelled(true); return; }
            }
            // Block interaction with decoration slots
            if (slot >= 9) {
                event.setCancelled(true);
                if (slot == TradeSession.CONFIRM_SLOT) {
                    session.resetConfirmation();
                    session.confirm(player);
                } else if (slot == TradeSession.CANCEL_SLOT) {
                    session.cancel(player);
                }
                return;
            }
            // If player modified their offer, reset confirmation
            session.resetConfirmation();
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        String title = event.getView().getTitle();
        if (title.contains("EconomyPlus Bank") || title.contains("Loan Manager") || title.contains("Withdraw Currency")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;
        String title = event.getView().getTitle();
        if (title.contains("Trade: ")) {
            TradeSession session = plugin.getTradeManager().getSession(player.getUniqueId());
            if (session != null && !session.isCancelled()) {
                session.cancel(player);
            }
        }
    }

    private void handleBankClick(Player player, int slot, String title) {
        // Route to deposit or withdraw submenus based on what was clicked
        if (title.contains("Main Menu")) {
            BankGUI.handleMainMenuClick(plugin, player, slot);
        } else if (title.contains("Deposit")) {
            BankGUI.handleDepositClick(plugin, player, slot);
        } else if (title.contains("Withdraw")) {
            BankGUI.handleWithdrawClick(plugin, player, slot);
        }
    }

    private void handleLoanClick(Player player, int slot) {
        LoanGUI.handleClick(plugin, player, slot);
    }

    private void handleCurrencyClick(Player player, int slot) {
        CurrencyGUI.handleClick(plugin, player, slot);
    }
}
