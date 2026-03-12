package com.economyplus.scoreboard;

import com.economyplus.EconomyPlus;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class ScoreboardListener implements Listener {

    private final EconomyPlus plugin;

    public ScoreboardListener(EconomyPlus plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        plugin.getServer().getScheduler().runTaskLater(plugin,
            () -> plugin.getScoreboardManager().updateScoreboard(event.getPlayer()), 20L);
    }
}
