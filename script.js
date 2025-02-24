class Stock {
    constructor(symbol, price) {
        this.symbol = symbol;
        this.price = price;
        this.previousClose = price;
        this.changePercent = 0;
    }

    updatePrice() {
        const volatility = 0.02; // 2% base volatility
        const movement = (Math.random() * 2 - 1) * volatility;
        const marketSentiment = (Math.random() - 0.5) * 0.01;
        
        const newPrice = this.price * (1 + movement + marketSentiment);
        this.price = Math.max(0.01, Math.round(newPrice * 100) / 100);
        this.changePercent = ((this.price - this.previousClose) / this.previousClose) * 100;
    }
}

class Portfolio {
    constructor() {
        this.holdings = new Map(); // symbol -> shares
    }

    buyStock(symbol, shares, price) {
        const currentShares = this.holdings.get(symbol) || 0;
        this.holdings.set(symbol, currentShares + shares);
    }

    sellStock(symbol, shares) {
        const currentShares = this.holdings.get(symbol) || 0;
        if (currentShares < shares) return false;
        
        const remainingShares = currentShares - shares;
        if (remainingShares === 0) {
            this.holdings.delete(symbol);
        } else {
            this.holdings.set(symbol, remainingShares);
        }
        return true;
    }

    getShares(symbol) {
        return this.holdings.get(symbol) || 0;
    }
}

class StockMarket {
    constructor() {
        this.stocks = new Map();
        this.initializeStocks();
    }

    initializeStocks() {
        const initialStocks = [
            ['AAPL', 150.50],
            ['GOOGL', 2750.25],
            ['MSFT', 285.75],
            ['AMZN', 3300.00],
            ['TSLA', 850.25],
            ['META', 330.50],
            ['NFLX', 450.75],
            ['NVDA', 420.25]
        ];

        for (const [symbol, price] of initialStocks) {
            this.stocks.set(symbol, new Stock(symbol, price));
        }
    }

    updatePrices() {
        for (const stock of this.stocks.values()) {
            stock.updatePrice();
        }
    }
}

class TradingSimulator {
    constructor() {
        this.market = new StockMarket();
        this.portfolio = new Portfolio();
        this.cash = 10000.00;
        
        this.marketTableBody = document.querySelector('#market-table tbody');
        this.portfolioTableBody = document.querySelector('#portfolio-table tbody');
        this.balanceElement = document.querySelector('#balance');
        this.portfolioValueElement = document.querySelector('#portfolio-value');

        this.startSimulation();
    }

    updateUI() {
        // Update market table
        this.marketTableBody.innerHTML = '';
        for (const stock of this.market.stocks.values()) {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${stock.symbol}</td>
                <td>$${stock.price.toFixed(2)}</td>
                <td class="${stock.changePercent >= 0 ? 'positive' : 'negative'}">
                    ${stock.changePercent.toFixed(2)}%
                </td>
                <td>
                    <button class="btn" onclick="simulator.showBuyDialog('${stock.symbol}')">
                        Buy
                    </button>
                </td>
            `;
            this.marketTableBody.appendChild(row);
        }

        // Update portfolio table
        this.portfolioTableBody.innerHTML = '';
        let portfolioValue = 0;

        for (const [symbol, shares] of this.portfolio.holdings) {
            const stock = this.market.stocks.get(symbol);
            if (stock) {
                const value = shares * stock.price;
                portfolioValue += value;

                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${symbol}</td>
                    <td>${shares}</td>
                    <td>$${value.toFixed(2)}</td>
                    <td>
                        <button class="btn" onclick="simulator.showSellDialog('${symbol}')">
                            Sell
                        </button>
                    </td>
                `;
                this.portfolioTableBody.appendChild(row);
            }
        }

        // Update balance and portfolio value
        this.balanceElement.textContent = `Balance: $${this.cash.toFixed(2)}`;
        this.portfolioValueElement.textContent = `Portfolio Value: $${portfolioValue.toFixed(2)}`;
    }

    startSimulation() {
        setInterval(() => {
            this.market.updatePrices();
            this.updateUI();
        }, 2000);
    }

    showBuyDialog(symbol) {
        const stock = this.market.stocks.get(symbol);
        if (!stock) return;

        const shares = prompt(`Enter number of shares to buy ${symbol} at $${stock.price.toFixed(2)}:`);
        if (shares === null) return;

        const quantity = parseInt(shares);
        if (isNaN(quantity) || quantity <= 0) {
            alert('Please enter a valid number of shares.');
            return;
        }

        const total = quantity * stock.price;
        if (total > this.cash) {
            alert('Insufficient funds for this purchase.');
            return;
        }

        this.portfolio.buyStock(symbol, quantity, stock.price);
        this.cash -= total;
        this.updateUI();
    }

    showSellDialog(symbol) {
        const stock = this.market.stocks.get(symbol);
        if (!stock) return;

        const currentShares = this.portfolio.getShares(symbol);
        const shares = prompt(`Enter number of shares to sell ${symbol} at $${stock.price.toFixed(2)}:`);
        if (shares === null) return;

        const quantity = parseInt(shares);
        if (isNaN(quantity) || quantity <= 0) {
            alert('Please enter a valid number of shares.');
            return;
        }

        if (quantity > currentShares) {
            alert('You don\'t have enough shares to sell.');
            return;
        }

        if (this.portfolio.sellStock(symbol, quantity)) {
            this.cash += quantity * stock.price;
            this.updateUI();
        }
    }
}

// Start the simulator when the page loads
let simulator;
document.addEventListener('DOMContentLoaded', () => {
    simulator = new TradingSimulator();
});
