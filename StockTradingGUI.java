import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.table.*;
import javax.swing.border.*;

public class StockTradingGUI implements StockMarket.MarketObserver {
    private JFrame frame;
    private Portfolio portfolio;
    private Map<String, Stock> marketStocks;
    private JLabel balanceLabel;
    private double cashBalance = 10000.00; // Starting balance
    private JPanel mainPanel;
    private DefaultTableModel stockTableModel;
    private DefaultTableModel portfolioTableModel;
    private Color primaryColor = new Color(240, 248, 255); // Alice Blue
    private Color secondaryColor = new Color(70, 130, 180); // Steel Blue
    private Color accentColor = new Color(95, 158, 160); // Cadet Blue

    public StockTradingGUI() {
        portfolio = new Portfolio();
        marketStocks = new HashMap<>();
        initializeFrame();
        initializeComponents();
    }

    private void initializeFrame() {
        frame = new JFrame("Stock Trading Simulator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(primaryColor);
    }

    private void initializeComponents() {
        mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(primaryColor);
        GridBagConstraints gbc = new GridBagConstraints();

        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        mainPanel.add(headerPanel, gbc);

        // Stock List Panel
        JPanel stockListPanel = createStockListPanel();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.6;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(stockListPanel, gbc);

        // Portfolio Panel
        JPanel portfolioPanel = createPortfolioPanel();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.4;
        mainPanel.add(portfolioPanel, gbc);

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(secondaryColor);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Left side - Balance
        balanceLabel = new JLabel("Balance: $" + String.format("%.2f", cashBalance));
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        balanceLabel.setForeground(Color.WHITE);
        headerPanel.add(balanceLabel, BorderLayout.WEST);

        // Right side - Portfolio Value
        JLabel portfolioValueLabel = new JLabel("Portfolio Value: $0.00");
        portfolioValueLabel.setFont(new Font("Arial", Font.BOLD, 16));
        portfolioValueLabel.setForeground(Color.WHITE);
        headerPanel.add(portfolioValueLabel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createStockListPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(secondaryColor),
            "Market Stocks",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14),
            secondaryColor
        ));
        panel.setBackground(primaryColor);

        // Create table model
        String[] columns = {"Symbol", "Price", "Change %", "Volume", "Action"};
        stockTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // Only allow editing of Action column
            }
        };

        JTable stockTable = new JTable(stockTableModel);
        stockTable.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
        stockTable.getColumnModel().getColumn(4).setCellEditor(
            new ButtonEditor(new JCheckBox(), this, "BUY")
        );

        // Style the table
        stockTable.setRowHeight(30);
        stockTable.setFont(new Font("Arial", Font.PLAIN, 12));
        stockTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        stockTable.setSelectionBackground(new Color(135, 206, 250)); // Light Sky Blue

        JScrollPane scrollPane = new JScrollPane(stockTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createPortfolioPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(secondaryColor),
            "Your Portfolio",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14),
            secondaryColor
        ));
        panel.setBackground(primaryColor);

        // Create table model
        String[] columns = {"Symbol", "Shares", "Avg Price", "Current Value", "Action"};
        portfolioTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // Only allow editing of Action column
            }
        };

        JTable portfolioTable = new JTable(portfolioTableModel);
        portfolioTable.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
        portfolioTable.getColumnModel().getColumn(4).setCellEditor(
            new ButtonEditor(new JCheckBox(), this, "SELL")
        );

        // Style the table
        portfolioTable.setRowHeight(30);
        portfolioTable.setFont(new Font("Arial", Font.PLAIN, 12));
        portfolioTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        portfolioTable.setSelectionBackground(new Color(135, 206, 250)); // Light Sky Blue

        JScrollPane scrollPane = new JScrollPane(portfolioTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    @Override
    public void onMarketUpdate(Map<String, Stock> stocks) {
        this.marketStocks = stocks;
        updateStockTable();
        updatePortfolioTable();
    }

    private void updateStockTable() {
        SwingUtilities.invokeLater(() -> {
            stockTableModel.setRowCount(0);
            for (Stock stock : marketStocks.values()) {
                Object[] row = {
                    stock.getSymbol(),
                    String.format("$%.2f", stock.getPrice()),
                    String.format("%.2f%%", stock.getChangePercent()),
                    stock.getVolume(),
                    "BUY"
                };
                stockTableModel.addRow(row);
            }
        });
    }

    private void updatePortfolioTable() {
        SwingUtilities.invokeLater(() -> {
            portfolioTableModel.setRowCount(0);
            Map<String, Integer> holdings = portfolio.getHoldings();
            double totalValue = 0;

            for (Map.Entry<String, Integer> entry : holdings.entrySet()) {
                String symbol = entry.getKey();
                int shares = entry.getValue();
                Stock stock = marketStocks.get(symbol);
                
                if (stock != null) {
                    double currentValue = shares * stock.getPrice();
                    totalValue += currentValue;
                    
                    Object[] row = {
                        symbol,
                        shares,
                        String.format("$%.2f", stock.getPrice()),
                        String.format("$%.2f", currentValue),
                        "SELL"
                    };
                    portfolioTableModel.addRow(row);
                }
            }

            // Update portfolio value label
            Component[] components = ((JPanel)mainPanel.getComponent(0)).getComponents();
            for (Component c : components) {
                if (c instanceof JLabel && ((JLabel)c).getText().startsWith("Portfolio Value")) {
                    ((JLabel)c).setText(String.format("Portfolio Value: $%.2f", totalValue));
                    break;
                }
            }
        });
    }

    // Button renderer for the tables
    static class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    // Button editor for the tables
    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        private final StockTradingGUI gui;
        private final String actionType;

        public ButtonEditor(JCheckBox checkBox, StockTradingGUI gui, String actionType) {
            super(checkBox);
            this.gui = gui;
            this.actionType = actionType;
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                // Handle the action based on the type (BUY/SELL)
                if (actionType.equals("BUY")) {
                    handleBuyAction();
                } else {
                    handleSellAction();
                }
            }
            isPushed = false;
            return label;
        }

        private void handleBuyAction() {
            // Get selected row from the table
            JTable table = (JTable) button.getParent();
            int row = table.getSelectedRow();
            if (row != -1) {
                String symbol = (String) stockTableModel.getValueAt(row, 0);
                Stock stock = marketStocks.get(symbol);
                if (stock != null) {
                    showTradeDialog(stock, true);
                }
            }
        }

        private void handleSellAction() {
            // Get selected row from the table
            JTable table = (JTable) button.getParent();
            int row = table.getSelectedRow();
            if (row != -1) {
                String symbol = (String) portfolioTableModel.getValueAt(row, 0);
                Stock stock = marketStocks.get(symbol);
                if (stock != null) {
                    showTradeDialog(stock, false);
                }
            }
        }

        private void showTradeDialog(Stock stock, boolean isBuy) {
            String action = isBuy ? "Buy" : "Sell";
            String shares = JOptionPane.showInputDialog(
                frame,
                String.format("Enter number of shares to %s for %s at $%.2f:", 
                    action.toLowerCase(), stock.getSymbol(), stock.getPrice())
            );

            try {
                int quantity = Integer.parseInt(shares);
                if (quantity <= 0) {
                    JOptionPane.showMessageDialog(frame, 
                        "Please enter a positive number of shares.");
                    return;
                }

                double total = quantity * stock.getPrice();
                if (isBuy) {
                    if (total > cashBalance) {
                        JOptionPane.showMessageDialog(frame, 
                            "Insufficient funds for this purchase.");
                        return;
                    }
                    portfolio.buyStock(stock.getSymbol(), quantity, stock.getPrice());
                    updateBalance(-total);
                } else {
                    if (quantity > portfolio.getShares(stock.getSymbol())) {
                        JOptionPane.showMessageDialog(frame, 
                            "You don't have enough shares to sell.");
                        return;
                    }
                    portfolio.sellStock(stock.getSymbol(), quantity, stock.getPrice());
                    updateBalance(total);
                }
                updatePortfolioTable();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(frame, 
                    "Please enter a valid number of shares.");
            }
        }
    }

    public void updateBalance(double amount) {
        cashBalance += amount;
        balanceLabel.setText("Balance: $" + String.format("%.2f", cashBalance));
    }
}
