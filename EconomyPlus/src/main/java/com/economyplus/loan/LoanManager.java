package com.economyplus.loan;

import com.economyplus.EconomyPlus;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class LoanManager {

    private final EconomyPlus plugin;
    private final Map<UUID, List<Loan>> activeLoans = new HashMap<>();
    private final File dataFile;
    private FileConfiguration dataConfig;

    public LoanManager(EconomyPlus plugin) {
        this.plugin = plugin;
        dataFile = new File(plugin.getDataFolder(), "loans.yml");
        loadData();
    }

    private void loadData() {
        if (!dataFile.exists()) {
            try { dataFile.getParentFile().mkdirs(); dataFile.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        if (dataConfig.contains("loans")) {
            for (String uuidStr : dataConfig.getConfigurationSection("loans").getKeys(false)) {
                UUID uuid = UUID.fromString(uuidStr);
                List<Loan> loans = new ArrayList<>();
                var section = dataConfig.getConfigurationSection("loans." + uuidStr);
                if (section != null) {
                    for (String idx : section.getKeys(false)) {
                        double principal = section.getDouble(idx + ".principal");
                        double remaining = section.getDouble(idx + ".remaining");
                        long dueTime = section.getLong(idx + ".dueTime");
                        double rate = section.getDouble(idx + ".rate");
                        loans.add(new Loan(principal, remaining, dueTime, rate));
                    }
                }
                if (!loans.isEmpty()) activeLoans.put(uuid, loans);
            }
        }
    }

    public void saveAll() {
        dataConfig.set("loans", null);
        for (Map.Entry<UUID, List<Loan>> entry : activeLoans.entrySet()) {
            String uuidStr = entry.getKey().toString();
            List<Loan> loans = entry.getValue();
            for (int i = 0; i < loans.size(); i++) {
                Loan l = loans.get(i);
                String path = "loans." + uuidStr + "." + i;
                dataConfig.set(path + ".principal", l.getPrincipal());
                dataConfig.set(path + ".remaining", l.getRemaining());
                dataConfig.set(path + ".dueTime", l.getDueTime());
                dataConfig.set(path + ".rate", l.getInterestRate());
            }
        }
        try { dataConfig.save(dataFile); } catch (IOException e) { e.printStackTrace(); }
    }

    public String takeLoan(UUID uuid, double amount) {
        List<Loan> loans = activeLoans.getOrDefault(uuid, new ArrayList<>());
        if (loans.size() >= plugin.getConfigManager().getMaxActiveLoans()) {
            return "You already have the maximum number of active loans (" + plugin.getConfigManager().getMaxActiveLoans() + ").";
        }
        double maxLoan = plugin.getBankManager().getMaxLoanAmount(uuid);
        if (amount > maxLoan) {
            return "Loan amount exceeds your limit of " + plugin.getEconomyManager().format(maxLoan) + ".";
        }
        if (amount <= 0) return "Invalid loan amount.";

        double rate = plugin.getBankManager().getEffectiveLoanRate();
        long dueTime = System.currentTimeMillis() + (plugin.getConfigManager().getLoanRepaymentDays() * 86400000L);
        Loan loan = new Loan(amount, amount, dueTime, rate);
        loans.add(loan);
        activeLoans.put(uuid, loans);
        plugin.getEconomyManager().deposit(uuid, amount);
        return null; // success
    }

    public String repayLoan(UUID uuid, int loanIndex, double amount) {
        List<Loan> loans = activeLoans.get(uuid);
        if (loans == null || loanIndex >= loans.size()) return "Invalid loan index.";
        Loan loan = loans.get(loanIndex);
        if (!plugin.getEconomyManager().withdraw(uuid, amount)) return "Insufficient funds.";
        loan.setRemaining(loan.getRemaining() - amount);
        if (loan.getRemaining() <= 0) {
            loans.remove(loanIndex);
            if (loans.isEmpty()) activeLoans.remove(uuid);
        }
        return null;
    }

    public List<Loan> getLoans(UUID uuid) {
        return activeLoans.getOrDefault(uuid, new ArrayList<>());
    }

    public void applyLoanPenalties() {
        long now = System.currentTimeMillis();
        double penaltyRate = plugin.getConfigManager().getLoanPenaltyPercent();
        for (Map.Entry<UUID, List<Loan>> entry : activeLoans.entrySet()) {
            for (Loan loan : entry.getValue()) {
                if (now > loan.getDueTime()) {
                    double penalty = loan.getRemaining() * penaltyRate;
                    loan.setRemaining(loan.getRemaining() + penalty);
                    loan.setDueTime(loan.getDueTime() + 86400000L); // extend by 1 day
                    // Notify if online
                    plugin.getServer().getScheduler().runTask(plugin, () -> {
                        var player = plugin.getServer().getPlayer(entry.getKey());
                        if (player != null) {
                            player.sendMessage(plugin.getConfigManager().getPrefix()
                                + plugin.getConfigManager().colorize("&cLoan overdue! Penalty applied: &e+"
                                + plugin.getEconomyManager().format(penalty)));
                        }
                    });
                }
            }
        }
    }

    public void startLoanPenaltyScheduler() {
        long ticks = 20L * 60 * 60; // every hour
        new BukkitRunnable() {
            @Override
            public void run() {
                if (plugin.getConfigManager().isLoanPenaltyEnabled()) {
                    applyLoanPenalties();
                }
            }
        }.runTaskTimer(plugin, ticks, ticks);
    }
}
