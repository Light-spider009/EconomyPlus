# EconomyPlus - Minecraft Economy Plugin

## Build Instructions

### Requirements
- Java 17 or higher (JDK)
- Maven 3.8+

### How to Build
```bash
cd EconomyPlus
mvn clean package
```
The compiled JAR will be at: `target/EconomyPlus.jar`

### Installation
1. Copy `EconomyPlus.jar` to your server's `plugins/` folder
2. Restart the server
3. Config auto-generates at `plugins/EconomyPlus/config.yml`

---

## Features Overview

### 💰 Money System
- Wallet balance per player
- Starting balance configurable
- `/balance [player]` — check funds
- `/pay <player> <amount>` — transfer money

### 🏦 Bank GUI (`/bank`)
- Deposit / withdraw in preset amounts or ALL
- Interest earned on deposits (dynamic rate)
- Connected to GDP/inflation system

### 📄 Physical Paper Currency (MOST IMPORTANT)
- Withdraw real paper note items (`/currency withdraw <denomination>`)
- Denominations: $1, $5, $10, $50, $100, $500, $1000
- **Right-click** a note → instantly deposited to wallet
- **Pick up** a dropped note → auto-deposited
- Notes use PersistentDataContainer (anti-duplication)
- Drop notes on the ground to give to players

### 📊 Loan System (`/loan`)
- Take loans up to 5× your net worth
- Dynamic interest rates linked to inflation
- Repayment timer with penalty for overdue loans
- Up to 3 active loans at once
- GUI interface from `/bank`

### 📈 Inflation & GDP System (`/gdp`)
- Server GDP = total wallet + bank deposits
- Inflation auto-adjusts based on GDP vs player count target
- High GDP → rates rise (money loses value)
- Low GDP → deflation (rates drop)
- Affects deposit interest AND loan rates live

### 🔄 Trade GUI (`/trade <player>`)
- Both players must `/trade accept` first (anti-scam)
- Both must click ✔ CONFIRM to execute
- Items returned if cancelled or window closed
- Inventory modified → confirmation resets

### 📺 Live Scoreboard
- Auto-updates every 5 seconds
- Shows: wallet, bank balance, inflation %, GDP, loans
- Configurable in config.yml

### ⚙️ Admin Commands
- `/ecoadmin give <player> <amount>`
- `/ecoadmin take <player> <amount>`
- `/ecoadmin set <player> <amount>`
- `/ecoadmin reset <player>`

---

## Configuration
Edit `plugins/EconomyPlus/config.yml` to customize:
- Currency name and symbol
- Starting balance
- Interest rates
- Loan limits
- Inflation parameters
- Scoreboard update speed
- Denominations

---

## Permissions
| Permission | Default | Description |
|---|---|---|
| `economyplus.bank` | true | Use /bank |
| `economyplus.pay` | true | Use /pay |
| `economyplus.loan` | true | Use /loan |
| `economyplus.trade` | true | Use /trade |
| `economyplus.currency` | true | Use /currency |
| `economyplus.gdp` | true | Use /gdp |
| `economyplus.admin` | op | Use /ecoadmin |

---

## Data Files (auto-created)
- `plugins/EconomyPlus/economy.yml` — wallet balances + inflation rate
- `plugins/EconomyPlus/bank.yml` — deposit balances
- `plugins/EconomyPlus/loans.yml` — active loans
