import java.util.*;
import javax.swing.Timer;

public class StockMarket {
    private Map<String, Stock> stocks;
    private List<MarketObserver> observers;
    private Timer updateTimer;
    private Random random;
    private static final int UPDATE_INTERVAL = 2000; // 2 seconds

    public interface MarketObserver {
        void onMarketUpdate(Map<String, Stock> stocks);
    }

    public StockMarket() {
        this.stocks = new HashMap<>();
        this.observers = new ArrayList<>();
        this.random = new Random();
        initializeStocks();
    }

    private void initializeStocks() {
        // Initialize with some popular stocks
        addStock("AAPL", 150.50);
        addStock("GOOGL", 2750.25);
        addStock("MSFT", 285.75);
        addStock("AMZN", 3300.00);
        addStock("TSLA", 850.25);
        addStock("META", 330.50);
        addStock("NFLX", 450.75);
        addStock("NVDA", 420.25);
    }

    public void addStock(String symbol, double initialPrice) {
        stocks.put(symbol, new Stock(symbol, initialPrice));
    }

    public void addObserver(MarketObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(MarketObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers() {
        for (MarketObserver observer : observers) {
            observer.onMarketUpdate(new HashMap<>(stocks));
        }
    }

    // Changed from private to public
    public void startMarketSimulation() {
        updateTimer = new Timer(UPDATE_INTERVAL, e -> {
            updateStockPrices();
            notifyObservers();
        });
        updateTimer.start();
    }

    private void updateStockPrices() {
        for (Stock stock : stocks.values()) {
            updateStockPrice(stock);
        }
    }

    private void updateStockPrice(Stock stock) {
        // Simulate more realistic price movements
        double volatility = 0.02; // 2% base volatility
        double movement = random.nextGaussian() * volatility;
        
        // Add some market sentiment bias (-0.5% to +0.5%)
        double marketSentiment = (random.nextDouble() - 0.5) * 0.01;
        
        // Calculate new price with both random movement and market sentiment
        double currentPrice = stock.getPrice();
        double newPrice = currentPrice * (1 + movement + marketSentiment);
        
        // Ensure price doesn't go below 0.01
        newPrice = Math.max(0.01, Math.round(newPrice * 100.0) / 100.0);
        
        stock.setPrice(newPrice);
    }

    public void stopMarketSimulation() {
        if (updateTimer != null) {
            updateTimer.stop();
        }
    }

    public Stock getStock(String symbol) {
        return stocks.get(symbol);
    }

    public Map<String, Stock> getAllStocks() {
        return new HashMap<>(stocks);
    }

    public double getStockPrice(String symbol) {
        Stock stock = stocks.get(symbol);
        return stock != null ? stock.getPrice() : 0.0;
    }

    // Method to get market summary
    public String getMarketSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("Market Summary:\n");
        summary.append("---------------\n");
        
        // Calculate market statistics
        double totalValue = 0;
        int gainers = 0;
        int losers = 0;
        
        for (Stock stock : stocks.values()) {
            totalValue += stock.getPrice();
            if (stock.getChangePercent() > 0) {
                gainers++;
            } else if (stock.getChangePercent() < 0) {
                losers++;
            }
            
            summary.append(String.format("%s: $%.2f (%+.2f%%)\n",
                stock.getSymbol(),
                stock.getPrice(),
                stock.getChangePercent()));
        }
        
        summary.append("---------------\n");
        summary.append(String.format("Gainers: %d, Losers: %d\n", gainers, losers));
        summary.append(String.format("Total Market Value: $%.2f\n", totalValue));
        
        return summary.toString();
    }

    // Method to get market trends
    public Map<String, Double> getMarketTrends() {
        Map<String, Double> trends = new HashMap<>();
        for (Stock stock : stocks.values()) {
            trends.put(stock.getSymbol(), stock.getChangePercent());
        }
        return trends;
    }
}

