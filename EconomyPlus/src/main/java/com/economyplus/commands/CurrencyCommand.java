package com.economyplus.commands;

import com.economyplus.EconomyPlus;
import com.economyplus.gui.CurrencyGUI;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CurrencyCommand implements CommandExecutor {
    private final EconomyPlus plugin;
    public CurrencyCommand(EconomyPlus plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        String prefix = plugin.getConfigManager().getPrefix();
        if (!(sender instanceof Player player)) { sender.sendMessage("Only players."); return true; }
        if (!player.hasPermission("economyplus.currency")) { player.sendMessage(plugin.getConfigManager().getNoPermission()); return true; }

        if (args.length == 0) { CurrencyGUI.openCurrencyMenu(plugin, player); return true; }

        if (args[0].equalsIgnoreCase("withdraw") && args.length >= 2) {
            try {
                int denom = Integer.parseInt(args[1]);
                String error = plugin.getCurrencyManager().withdrawCurrency(player, denom);
                if (error == null) player.sendMessage(prefix + ChatColor.GREEN + "Withdrew " + plugin.getConfigManager().getCurrencySymbol() + denom + " note!");
                else player.sendMessage(prefix + ChatColor.RED + error);
            } catch (NumberFormatException e) { player.sendMessage(prefix + ChatColor.RED + "Invalid denomination."); }
        } else {
            CurrencyGUI.openCurrencyMenu(plugin, player);
        }
        return true;
    }
}
