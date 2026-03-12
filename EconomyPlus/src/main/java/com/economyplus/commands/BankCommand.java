package com.economyplus.commands;

import com.economyplus.EconomyPlus;
import com.economyplus.gui.BankGUI;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BankCommand implements CommandExecutor {
    private final EconomyPlus plugin;
    public BankCommand(EconomyPlus plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }
        if (!player.hasPermission("economyplus.bank")) {
            player.sendMessage(plugin.getConfigManager().getNoPermission());
            return true;
        }
        BankGUI.openMainMenu(plugin, player);
        return true;
    }
}
