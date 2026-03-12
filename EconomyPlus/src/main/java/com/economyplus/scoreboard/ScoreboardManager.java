package com.economyplus.scoreboard;

import com.economyplus.EconomyPlus;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class ScoreboardManager {

    private final EconomyPlus plugin;

    public ScoreboardManager(EconomyPlus plugin) {
        this.plugin = plugin;
    }

    public void updateScoreboard(Player player) {
        if (!plugin.getConfigManager().isScoreboardEnabled()) return;

        org.bukkit.scoreboard.ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getNewScoreboard();

        Objective obj = board.registerNewObjective("economyplus", Criteria.DUMMY,
            ChatColor.GOLD + "" + ChatColor.BOLD + "EconomyPlus");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        double balance = plugin.getEconomyManager().getBalance(player.getUniqueId());
        double deposit = plugin.getBankManager().getDeposit(player.getUniqueId());
        double inflation = plugin.getEconomyManager().getCurrentInflationRate();
        double gdp = plugin.getEconomyManager().getServerGDP();
        List<com.economyplus.loan.Loan> loans = plugin.getLoanManager().getLoans(player.getUniqueId());

        int line = 15;
        setScore(obj, ChatColor.YELLOW + "» Economy Stats «", line--);
        setScore(obj, ChatColor.GRAY + "  ", line--);
        setScore(obj, ChatColor.WHITE + "Wallet: " + ChatColor.GREEN + plugin.getEconomyManager().format(balance), line--);
        setScore(obj, ChatColor.WHITE + "Bank: " + ChatColor.AQUA + plugin.getEconomyManager().format(deposit), line--);
        setScore(obj, ChatColor.GRAY + " ", line--);
        setScore(obj, ChatColor.WHITE + "Inflation: " + (inflation >= 0 ? ChatColor.RED : ChatColor.GREEN)
            + String.format("%.2f%%", inflation * 100), line--);
        setScore(obj, ChatColor.WHITE + "Server GDP: " + ChatColor.GOLD + plugin.getEconomyManager().format(gdp), line--);
        if (!loans.isEmpty()) {
            setScore(obj, ChatColor.GRAY + "  ", line--);
            setScore(obj, ChatColor.RED + "Loans: " + ChatColor.WHITE + loans.size() + " active", line--);
            double totalOwed = loans.stream().mapToDouble(com.economyplus.loan.Loan::getRemaining).sum();
            setScore(obj, ChatColor.RED + "Owed: " + ChatColor.WHITE + plugin.getEconomyManager().format(totalOwed), line--);
        }
        setScore(obj, ChatColor.GRAY + "   ", line--);
        setScore(obj, ChatColor.GRAY + "play.yourserver.net", line);

        player.setScoreboard(board);
    }

    private void setScore(Objective obj, String text, int score) {
        // Truncate to 40 chars (Scoreboard limit)
        if (text.length() > 40) text = text.substring(0, 40);
        obj.getScore(text).setScore(score);
    }

    public void startUpdateScheduler() {
        int seconds = plugin.getConfigManager().getScoreboardUpdateSeconds();
        long ticks = seconds * 20L;
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    updateScoreboard(player);
                }
            }
        }.runTaskTimer(plugin, 20L, ticks);
    }
}
