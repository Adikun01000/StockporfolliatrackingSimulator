import java.util.*;

public class Portfolio {
    private Map<String, Integer> holdings; // Map of stock symbol to number of shares
    private List<Transaction> transactions;
    private double initialValue;
    private double currentValue;

    public Portfolio() {
        this.holdings = new HashMap<>();
        this.transactions = new ArrayList<>();
        this.initialValue = 0.0;
        this.currentValue = 0.0;
    }

    // Inner class to represent a transaction
    public static class Transaction {
        private String symbol;
        private int shares;
        private double price;
        private String type; // "BUY" or "SELL"
        private Date timestamp;

        public Transaction(String symbol, int shares, double price, String type) {
            this.symbol = symbol;
            this.shares = shares;
            this.price = price;
            this.type = type;
            this.timestamp = new Date();
        }

        public String getSymbol() { return symbol; }
        public int getShares() { return shares; }
        public double getPrice() { return price; }
        public String getType() { return type; }
        public Date getTimestamp() { return timestamp; }

        @Override
        public String toString() {
            return String.format("%s: %s %d shares of %s at $%.2f", 
                timestamp, type, shares, symbol, price);
        }
    }

    // Method to buy stocks
    public boolean buyStock(String symbol, int shares, double price) {
        // Add to holdings
        holdings.put(symbol, holdings.getOrDefault(symbol, 0) + shares);
        
        // Record transaction
        transactions.add(new Transaction(symbol, shares, price, "BUY"));
        
        // Update portfolio value
        double transactionValue = shares * price;
        currentValue += transactionValue;
        
        return true;
    }

    // Method to sell stocks
    public boolean sellStock(String symbol, int shares, double price) {
        // Check if we have enough shares to sell
        int currentShares = holdings.getOrDefault(symbol, 0);
        if (currentShares < shares) {
            return false;
        }

        // Update holdings
        int remainingShares = currentShares - shares;
        if (remainingShares == 0) {
            holdings.remove(symbol);
        } else {
            holdings.put(symbol, remainingShares);
        }

        // Record transaction
        transactions.add(new Transaction(symbol, shares, price, "SELL"));

        // Update portfolio value
        double transactionValue = shares * price;
        currentValue -= transactionValue;

        return true;
    }

    // Get number of shares for a specific stock
    public int getShares(String symbol) {
        return holdings.getOrDefault(symbol, 0);
    }

    // Get all holdings
    public Map<String, Integer> getHoldings() {
        return new HashMap<>(holdings);
    }

    // Get transaction history
    public List<Transaction> getTransactionHistory() {
        return new ArrayList<>(transactions);
    }

    // Calculate total value of portfolio given current stock prices
    public double calculateTotalValue(Map<String, Stock> currentStocks) {
        double totalValue = 0.0;
        for (Map.Entry<String, Integer> holding : holdings.entrySet()) {
            String symbol = holding.getKey();
            int shares = holding.getValue();
            Stock stock = currentStocks.get(symbol);
            if (stock != null) {
                totalValue += shares * stock.getPrice();
            }
        }
        this.currentValue = totalValue;
        return totalValue;
    }

    // Calculate profit/loss
    public double getProfitLoss() {
        return currentValue - initialValue;
    }

    // Get profit/loss percentage
    public double getProfitLossPercentage() {
        if (initialValue == 0) return 0.0;
        return ((currentValue - initialValue) / initialValue) * 100;
    }

    // Method to get portfolio summary
    public String getPortfolioSummary(Map<String, Stock> currentStocks) {
        StringBuilder summary = new StringBuilder();
        summary.append("Portfolio Summary:\n");
        summary.append("----------------\n");
        
        double totalValue = 0.0;
        for (Map.Entry<String, Integer> holding : holdings.entrySet()) {
            String symbol = holding.getKey();
            int shares = holding.getValue();
            Stock stock = currentStocks.get(symbol);
            if (stock != null) {
                double value = shares * stock.getPrice();
                totalValue += value;
                summary.append(String.format("%s: %d shares @ $%.2f = $%.2f\n",
                    symbol, shares, stock.getPrice(), value));
            }
        }
        
        summary.append("----------------\n");
        summary.append(String.format("Total Value: $%.2f\n", totalValue));
        summary.append(String.format("Profit/Loss: $%.2f (%.2f%%)\n",
            getProfitLoss(), getProfitLossPercentage()));
        
        return summary.toString();
    }
}
