package com.economyplus.config;

import com.economyplus.EconomyPlus;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class ConfigManager {

    private final EconomyPlus plugin;
    private FileConfiguration config;

    public ConfigManager(EconomyPlus plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        plugin.reloadConfig();
        config = plugin.getConfig();
    }

    public String getCurrencyName() { return config.getString("economy.currency-name", "Dollar"); }
    public String getCurrencySymbol() { return config.getString("economy.currency-symbol", "$"); }
    public double getStartingBalance() { return config.getDouble("economy.starting-balance", 500.0); }
    public double getMaxBalance() { return config.getDouble("economy.max-balance", 1_000_000_000.0); }

    public double getBaseDepositInterestRate() { return config.getDouble("bank.base-deposit-interest-rate", 0.02); }
    public double getBaseLoanInterestRate() { return config.getDouble("bank.base-loan-interest-rate", 0.05); }
    public int getInterestCycleHours() { return config.getInt("bank.interest-cycle-hours", 24); }
    public double getMaxLoanMultiplier() { return config.getDouble("bank.max-loan-multiplier", 5.0); }
    public double getMaxDeposit() { return config.getDouble("bank.max-deposit", 10_000_000.0); }
    public double getLoanPenaltyPercent() { return config.getDouble("bank.loan-penalty-percent", 0.10); }

    public boolean isInflationEnabled() { return config.getBoolean("inflation.enabled", true); }
    public double getTargetGDP() { return config.getDouble("inflation.target-gdp", 1_000_000.0); }
    public int getInflationCheckIntervalMinutes() { return config.getInt("inflation.check-interval-minutes", 60); }
    public double getMaxInflationRate() { return config.getDouble("inflation.max-inflation-rate", 0.15); }
    public double getMinInflationRate() { return config.getDouble("inflation.min-inflation-rate", -0.05); }
    public double getGdpPerPlayerTarget() { return config.getDouble("inflation.gdp-per-player-target", 5000.0); }

    public int getLoanRepaymentDays() { return config.getInt("loan.repayment-days", 7); }
    public boolean isLoanPenaltyEnabled() { return config.getBoolean("loan.penalty-enabled", true); }
    public int getMaxActiveLoans() { return config.getInt("loan.max-active-loans", 3); }

    public boolean isPaperCurrencyEnabled() { return config.getBoolean("currency.paper-currency-enabled", true); }
    public List<Integer> getDenominations() { return config.getIntegerList("currency.denominations"); }

    public boolean isScoreboardEnabled() { return config.getBoolean("scoreboard.enabled", true); }
    public int getScoreboardUpdateSeconds() { return config.getInt("scoreboard.update-interval-seconds", 5); }

    public String getPrefix() { return colorize(config.getString("messages.prefix", "&6[&eEconomy+&6] &r")); }
    public String getNoPermission() { return getPrefix() + colorize(config.getString("messages.no-permission", "&cNo permission!")); }
    public String getPlayerNotFound() { return getPrefix() + colorize(config.getString("messages.player-not-found", "&cPlayer not found!")); }

    public static String colorize(String text) {
        return text.replace("&", "\u00a7");
    }
}
