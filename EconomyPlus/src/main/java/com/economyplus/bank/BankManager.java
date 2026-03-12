package com.economyplus.bank;

import com.economyplus.EconomyPlus;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BankManager {

    private final EconomyPlus plugin;
    private final Map<UUID, Double> deposits = new HashMap<>();
    private final File dataFile;
    private FileConfiguration dataConfig;

    public BankManager(EconomyPlus plugin) {
        this.plugin = plugin;
        dataFile = new File(plugin.getDataFolder(), "bank.yml");
        loadData();
    }

    private void loadData() {
        if (!dataFile.exists()) {
            try { dataFile.getParentFile().mkdirs(); dataFile.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        if (dataConfig.contains("deposits")) {
            for (String key : dataConfig.getConfigurationSection("deposits").getKeys(false)) {
                deposits.put(UUID.fromString(key), dataConfig.getDouble("deposits." + key));
            }
        }
    }

    public void saveAll() {
        for (Map.Entry<UUID, Double> entry : deposits.entrySet()) {
            dataConfig.set("deposits." + entry.getKey().toString(), entry.getValue());
        }
        try { dataConfig.save(dataFile); } catch (IOException e) { e.printStackTrace(); }
    }

    public double getDeposit(UUID uuid) {
        return deposits.getOrDefault(uuid, 0.0);
    }

    public boolean depositToBank(UUID uuid, double amount) {
        if (amount <= 0) return false;
        double currentDeposit = getDeposit(uuid);
        if (currentDeposit + amount > plugin.getConfigManager().getMaxDeposit()) return false;
        if (!plugin.getEconomyManager().withdraw(uuid, amount)) return false;
        deposits.put(uuid, currentDeposit + amount);
        return true;
    }

    public boolean withdrawFromBank(UUID uuid, double amount) {
        if (amount <= 0) return false;
        double currentDeposit = getDeposit(uuid);
        if (currentDeposit < amount) return false;
        deposits.put(uuid, currentDeposit - amount);
        plugin.getEconomyManager().deposit(uuid, amount);
        return true;
    }

    public double getEffectiveDepositRate() {
        double base = plugin.getConfigManager().getBaseDepositInterestRate();
        double inflation = plugin.getEconomyManager().getCurrentInflationRate();
        // Deposit rate slightly below inflation to encourage spending
        return Math.max(0.001, base - (inflation * 0.3));
    }

    public double getEffectiveLoanRate() {
        double base = plugin.getConfigManager().getBaseLoanInterestRate();
        double inflation = plugin.getEconomyManager().getCurrentInflationRate();
        // Loan rate above inflation
        return base + (inflation * 0.5);
    }

    public void applyDepositInterest() {
        double rate = getEffectiveDepositRate();
        for (Map.Entry<UUID, Double> entry : deposits.entrySet()) {
            double interest = entry.getValue() * rate;
            entry.setValue(entry.getValue() + interest);
            // Notify player if online
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                var player = plugin.getServer().getPlayer(entry.getKey());
                if (player != null) {
                    player.sendMessage(plugin.getConfigManager().getPrefix()
                            + plugin.getConfigManager().colorize("&aBank interest applied: &e+"
                            + plugin.getEconomyManager().format(interest)
                            + " &7(rate: " + String.format("%.2f%%", rate * 100) + ")"));
                }
            });
        }
    }

    public void startInterestScheduler() {
        long ticks = plugin.getConfigManager().getInterestCycleHours() * 3600L * 20L;
        new BukkitRunnable() {
            @Override
            public void run() {
                applyDepositInterest();
            }
        }.runTaskTimer(plugin, ticks, ticks);
    }

    public double getTotalDeposits() {
        return deposits.values().stream().mapToDouble(Double::doubleValue).sum();
    }

    public double getMaxLoanAmount(UUID uuid) {
        double balance = plugin.getEconomyManager().getBalance(uuid);
        double deposit = getDeposit(uuid);
        return (balance + deposit) * plugin.getConfigManager().getMaxLoanMultiplier();
    }
}
