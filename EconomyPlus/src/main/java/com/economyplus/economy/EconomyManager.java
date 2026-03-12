package com.economyplus.economy;

import com.economyplus.EconomyPlus;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EconomyManager {

    private final EconomyPlus plugin;
    private final Map<UUID, Double> balances = new HashMap<>();
    private final File dataFile;
    private FileConfiguration dataConfig;

    // GDP & Inflation
    private double serverGDP = 0;
    private double currentInflationRate = 0.02; // 2% default
    private double totalMoneySupply = 0;

    public EconomyManager(EconomyPlus plugin) {
        this.plugin = plugin;
        dataFile = new File(plugin.getDataFolder(), "economy.yml");
        loadData();
        recalculateGDP();
    }

    private void loadData() {
        if (!dataFile.exists()) {
            try { dataFile.getParentFile().mkdirs(); dataFile.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        if (dataConfig.contains("balances")) {
            for (String key : dataConfig.getConfigurationSection("balances").getKeys(false)) {
                balances.put(UUID.fromString(key), dataConfig.getDouble("balances." + key));
            }
        }
        currentInflationRate = dataConfig.getDouble("inflation-rate", 0.02);
    }

    public void saveAll() {
        for (Map.Entry<UUID, Double> entry : balances.entrySet()) {
            dataConfig.set("balances." + entry.getKey().toString(), entry.getValue());
        }
        dataConfig.set("inflation-rate", currentInflationRate);
        dataConfig.set("server-gdp", serverGDP);
        try { dataConfig.save(dataFile); } catch (IOException e) { e.printStackTrace(); }
    }

    public double getBalance(UUID uuid) {
        return balances.getOrDefault(uuid, plugin.getConfigManager().getStartingBalance());
    }

    public boolean deposit(UUID uuid, double amount) {
        if (amount <= 0) return false;
        double newBal = getBalance(uuid) + amount;
        if (newBal > plugin.getConfigManager().getMaxBalance()) return false;
        balances.put(uuid, newBal);
        recalculateGDP();
        return true;
    }

    public boolean withdraw(UUID uuid, double amount) {
        if (amount <= 0) return false;
        double current = getBalance(uuid);
        if (current < amount) return false;
        balances.put(uuid, current - amount);
        recalculateGDP();
        return true;
    }

    public boolean setBalance(UUID uuid, double amount) {
        if (amount < 0 || amount > plugin.getConfigManager().getMaxBalance()) return false;
        balances.put(uuid, amount);
        recalculateGDP();
        return true;
    }

    public boolean transfer(UUID from, UUID to, double amount) {
        if (!withdraw(from, amount)) return false;
        deposit(to, amount);
        return true;
    }

    public void recalculateGDP() {
        totalMoneySupply = balances.values().stream().mapToDouble(Double::doubleValue).sum();
        // GDP = total money supply + bank deposits (simplified)
        serverGDP = totalMoneySupply + plugin.getBankManager().getTotalDeposits();
        adjustInflation();
    }

    private void adjustInflation() {
        if (!plugin.getConfigManager().isInflationEnabled()) return;
        int onlinePlayers = Math.max(1, plugin.getServer().getOnlinePlayers().size());
        double targetGDP = plugin.getConfigManager().getGdpPerPlayerTarget() * onlinePlayers;
        double gdpRatio = serverGDP / targetGDP;

        // If GDP is too high → inflation rises; too low → deflation/lower rates
        if (gdpRatio > 1.5) {
            currentInflationRate = Math.min(plugin.getConfigManager().getMaxInflationRate(), currentInflationRate + 0.005);
        } else if (gdpRatio < 0.5) {
            currentInflationRate = Math.max(plugin.getConfigManager().getMinInflationRate(), currentInflationRate - 0.005);
        } else {
            // Slowly normalize toward 2%
            if (currentInflationRate > 0.02) currentInflationRate -= 0.001;
            else if (currentInflationRate < 0.02) currentInflationRate += 0.001;
        }
    }

    public void startInflationScheduler() {
        int intervalMinutes = plugin.getConfigManager().getInflationCheckIntervalMinutes();
        long ticks = intervalMinutes * 60L * 20L;
        new BukkitRunnable() {
            @Override
            public void run() {
                recalculateGDP();
            }
        }.runTaskTimer(plugin, ticks, ticks);
    }

    public double getServerGDP() { return serverGDP; }
    public double getCurrentInflationRate() { return currentInflationRate; }
    public double getTotalMoneySupply() { return totalMoneySupply; }

    public String format(double amount) {
        return plugin.getConfigManager().getCurrencySymbol() + String.format("%,.2f", amount);
    }
}
