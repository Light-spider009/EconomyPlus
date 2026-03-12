package com.economyplus.commands;

import com.economyplus.EconomyPlus;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TradeCommand implements CommandExecutor {
    private final EconomyPlus plugin;
    public TradeCommand(EconomyPlus plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        String prefix = plugin.getConfigManager().getPrefix();
        if (!(sender instanceof Player player)) { sender.sendMessage("Only players."); return true; }
        if (!player.hasPermission("economyplus.trade")) { player.sendMessage(plugin.getConfigManager().getNoPermission()); return true; }
        if (args.length == 0) { player.sendMessage(prefix + ChatColor.RED + "Usage: /trade <player> | /trade accept <player>"); return true; }

        if (args[0].equalsIgnoreCase("accept")) {
            if (args.length < 2) { player.sendMessage(prefix + ChatColor.RED + "Usage: /trade accept <player>"); return true; }
            Player requester = plugin.getServer().getPlayer(args[1]);
            if (requester == null) { player.sendMessage(plugin.getConfigManager().getPlayerNotFound()); return true; }
            plugin.getTradeManager().acceptRequest(player, requester);
        } else {
            Player target = plugin.getServer().getPlayer(args[0]);
            if (target == null) { player.sendMessage(plugin.getConfigManager().getPlayerNotFound()); return true; }
            if (target.equals(player)) { player.sendMessage(prefix + ChatColor.RED + "You can't trade with yourself!"); return true; }
            plugin.getTradeManager().sendRequest(player, target);
        }
        return true;
    }
}
