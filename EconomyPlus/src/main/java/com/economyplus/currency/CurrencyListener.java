package com.economyplus.currency;

import com.economyplus.EconomyPlus;
import org.bukkit.ChatColor;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class CurrencyListener implements Listener {

    private final EconomyPlus plugin;

    public CurrencyListener(EconomyPlus plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Player player = event.getPlayer();
        var item = player.getInventory().getItemInMainHand();
        if (plugin.getCurrencyManager().isCurrencyNote(item)) {
            event.setCancelled(true);
            plugin.getCurrencyManager().depositCurrencyNote(player, item);
        }
    }

    // When a player picks up a currency note dropped on the ground, it auto-deposits
    @EventHandler
    public void onPickupCurrency(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        Item item = event.getItem();
        int value = plugin.getCurrencyManager().getCurrencyValue(item.getItemStack());
        if (value > 0) {
            event.setCancelled(true);
            item.remove();
            plugin.getEconomyManager().deposit(player.getUniqueId(), value);
            player.sendMessage(plugin.getConfigManager().getPrefix()
                + plugin.getConfigManager().colorize("&aPicked up currency note: &e+"
                + plugin.getEconomyManager().format(value)));
            plugin.getScoreboardManager().updateScoreboard(player);
        }
    }
}
