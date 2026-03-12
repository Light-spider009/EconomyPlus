package com.economyplus.trade;

import com.economyplus.EconomyPlus;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TradeManager {

    private final EconomyPlus plugin;
    // Pending trade requests: requester -> target
    private final Map<UUID, UUID> pendingRequests = new HashMap<>();
    // Active trades: player1 -> TradeSession
    private final Map<UUID, TradeSession> activeTrades = new HashMap<>();

    public TradeManager(EconomyPlus plugin) {
        this.plugin = plugin;
    }

    public void sendRequest(Player from, Player to) {
        pendingRequests.put(from.getUniqueId(), to.getUniqueId());
        from.sendMessage(plugin.getConfigManager().getPrefix()
            + plugin.getConfigManager().colorize("&eTrade request sent to &b" + to.getName() + "&e."));
        to.sendMessage(plugin.getConfigManager().getPrefix()
            + plugin.getConfigManager().colorize("&b" + from.getName() + " &ewants to trade with you! Use &a/trade accept " + from.getName() + " &eto accept."));
    }

    public void acceptRequest(Player accepter, Player requester) {
        if (!pendingRequests.containsKey(requester.getUniqueId())
                || !pendingRequests.get(requester.getUniqueId()).equals(accepter.getUniqueId())) {
            accepter.sendMessage(plugin.getConfigManager().getPrefix()
                + plugin.getConfigManager().colorize("&cNo pending trade request from " + requester.getName()));
            return;
        }
        pendingRequests.remove(requester.getUniqueId());
        TradeSession session = new TradeSession(plugin, requester, accepter);
        activeTrades.put(requester.getUniqueId(), session);
        activeTrades.put(accepter.getUniqueId(), session);
        session.openForBoth();
    }

    public TradeSession getSession(UUID uuid) {
        return activeTrades.get(uuid);
    }

    public void closeSession(UUID uuid) {
        TradeSession session = activeTrades.remove(uuid);
        if (session != null) {
            activeTrades.remove(session.getOtherUUID(uuid));
        }
    }

    public Map<UUID, UUID> getPendingRequests() { return pendingRequests; }
    public Map<UUID, TradeSession> getActiveTrades() { return activeTrades; }
}
