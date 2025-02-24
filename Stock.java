public class Stock {
    private String symbol;
    private double price;
    private double previousClose;
    private int volume;
    private double high;
    private double low;
    private double changePercent;

    public Stock(String symbol, double price) {
        this.symbol = symbol;
        this.price = price;
        this.previousClose = price;
        this.volume = 0;
        this.high = price;
        this.low = price;
        this.changePercent = 0.0;
    }

    // Getters and setters
    public String getSymbol() {
        return symbol;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        if (price > high) {
            high = price;
        }
        if (price < low) {
            low = price;
        }
        this.changePercent = ((price - previousClose) / previousClose) * 100;
        this.price = price;
    }

    public double getPreviousClose() {
        return previousClose;
    }

    public void setPreviousClose(double previousClose) {
        this.previousClose = previousClose;
    }

    public int getVolume() {
        return volume;
    }

    public void incrementVolume(int shares) {
        this.volume += shares;
    }

    public double getHigh() {
        return high;
    }

    public double getLow() {
        return low;
    }

    public double getChangePercent() {
        return changePercent;
    }

    // Method to simulate price movement
    public void updatePrice() {
        // Simulate random price movement between -2% and +2%
        double movement = (Math.random() * 4 - 2) / 100;
        double newPrice = price * (1 + movement);
        setPrice(Math.round(newPrice * 100.0) / 100.0); // Round to 2 decimal places
    }

    @Override
    public String toString() {
        return String.format("%s: $%.2f (%+.2f%%)", symbol, price, changePercent);
    }
}
