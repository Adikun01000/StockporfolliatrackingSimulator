import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        try {
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Set custom colors for components
            UIManager.put("Panel.background", new Color(240, 248, 255)); // Alice Blue
            UIManager.put("Button.background", new Color(70, 130, 180)); // Steel Blue
            UIManager.put("Button.foreground", Color.WHITE);
            UIManager.put("Button.font", new Font("Arial", Font.BOLD, 12));
            UIManager.put("Label.font", new Font("Arial", Font.PLAIN, 12));
            UIManager.put("Table.font", new Font("Arial", Font.PLAIN, 12));
            UIManager.put("TableHeader.font", new Font("Arial", Font.BOLD, 12));
            
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            // Create and start the stock market simulation
            StockMarket stockMarket = new StockMarket();
            
            // Create the main trading GUI
            StockTradingGUI tradingGUI = new StockTradingGUI();
            
            // Add the trading GUI as an observer to the stock market
            stockMarket.addObserver(tradingGUI);
            
            // Start updating stock prices
            stockMarket.startMarketSimulation();
        });
    }
}
