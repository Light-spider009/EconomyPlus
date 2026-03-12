package com.economyplus;

import com.economyplus.bank.BankManager;
import com.economyplus.commands.*;
import com.economyplus.config.ConfigManager;
import com.economyplus.currency.CurrencyManager;
import com.economyplus.economy.EconomyManager;
import com.economyplus.gui.GUIListener;
import com.economyplus.loan.LoanManager;
import com.economyplus.scoreboard.ScoreboardManager;
import com.economyplus.trade.TradeManager;
import org.bukkit.plugin.java.JavaPlugin;

public class EconomyPlus extends JavaPlugin {

    private static EconomyPlus instance;
    private ConfigManager configManager;
    private EconomyManager economyManager;
    private BankManager bankManager;
    private LoanManager loanManager;
    private CurrencyManager currencyManager;
    private ScoreboardManager scoreboardManager;
    private TradeManager tradeManager;

    @Override
    public void onEnable() {
        instance = this;

        // Save default configs
        saveDefaultConfig();

        // Initialize managers
        configManager = new ConfigManager(this);
        bankManager = new BankManager(this);
        economyManager = new EconomyManager(this);
        loanManager = new LoanManager(this);
        currencyManager = new CurrencyManager(this);
        scoreboardManager = new ScoreboardManager(this);
        tradeManager = new TradeManager(this);

        // Register commands
        getCommand("bank").setExecutor(new BankCommand(this));
        getCommand("balance").setExecutor(new BalanceCommand(this));
        getCommand("pay").setExecutor(new PayCommand(this));
        getCommand("loan").setExecutor(new LoanCommand(this));
        getCommand("trade").setExecutor(new TradeCommand(this));
        getCommand("currency").setExecutor(new CurrencyCommand(this));
        getCommand("gdp").setExecutor(new GDPCommand(this));
        getCommand("ecoAdmin").setExecutor(new AdminCommand(this));

        // Register listeners
        getServer().getPluginManager().registerEvents(new GUIListener(this), this);
        getServer().getPluginManager().registerEvents(new com.economyplus.currency.CurrencyListener(this), this);
        getServer().getPluginManager().registerEvents(new com.economyplus.scoreboard.ScoreboardListener(this), this);

        // Start schedulers
        bankManager.startInterestScheduler();
        economyManager.startInflationScheduler();
        loanManager.startLoanPenaltyScheduler();
        scoreboardManager.startUpdateScheduler();

        getLogger().info("EconomyPlus enabled! Currency, Bank, Loans, and GDP system active.");
    }

    @Override
    public void onDisable() {
        if (economyManager != null) economyManager.saveAll();
        if (bankManager != null) bankManager.saveAll();
        if (loanManager != null) loanManager.saveAll();
        getLogger().info("EconomyPlus disabled. All data saved.");
    }

    public static EconomyPlus getInstance() { return instance; }
    public ConfigManager getConfigManager() { return configManager; }
    public EconomyManager getEconomyManager() { return economyManager; }
    public BankManager getBankManager() { return bankManager; }
    public LoanManager getLoanManager() { return loanManager; }
    public CurrencyManager getCurrencyManager() { return currencyManager; }
    public ScoreboardManager getScoreboardManager() { return scoreboardManager; }
    public TradeManager getTradeManager() { return tradeManager; }
}
