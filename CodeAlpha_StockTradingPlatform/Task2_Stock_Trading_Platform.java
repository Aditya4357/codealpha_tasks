import java.io.*;
import java.util.*;

class Stock {
    private final String symbol;
    private final String company;
    private double price;

    public Stock(String symbol, String company, double price) {
        this.symbol = symbol;
        this.company = company;
        this.price = price;
    }

    public String getSymbol() { return symbol; }
    public String getCompany() { return company; }
    public double getPrice() { return price; }

    public void setPrice(double price) {
        this.price = price;
    }
}

class Holding {
    private final Stock stock;
    private int quantity;
    private double investedAmount;

    public Holding(Stock stock, int quantity, double investedAmount) {
        this.stock = stock;
        this.quantity = quantity;
        this.investedAmount = investedAmount;
    }

    public Stock getStock() { return stock; }
    public int getQuantity() { return quantity; }
    public double getInvestedAmount() { return investedAmount; }

    public void buy(int qty, double amount) {
        quantity += qty;
        investedAmount += amount;
    }

    public void sell(int qty, double amount) {
        if (qty > quantity) throw new IllegalArgumentException("Not enough shares.");
        double averageCost = investedAmount / quantity;
        investedAmount -= averageCost * qty;
        quantity -= qty;
    }
}

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final Map<String, Stock> market = new LinkedHashMap<>();
    private static final Map<String, Holding> portfolio = new LinkedHashMap<>();
    private static double cash = 100000.00;

    public static void main(String[] args) {
        initializeMarket();

        while (true) {
            System.out.println("\n===== STOCK TRADING PLATFORM =====");
            System.out.println("1. View market");
            System.out.println("2. Buy stock");
            System.out.println("3. Sell stock");
            System.out.println("4. View portfolio");
            System.out.println("5. Save portfolio");
            System.out.println("6. Exit");
            System.out.print("Choose: ");

            switch (scanner.nextLine().trim()) {
                case "1" -> showMarket();
                case "2" -> buyStock();
                case "3" -> sellStock();
                case "4" -> showPortfolio();
                case "5" -> savePortfolio();
                case "6" -> {
                    savePortfolio();
                    System.out.println("Session ended.");
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private static void initializeMarket() {
        addStock(new Stock("AAPL", "Apple", 210.50));
        addStock(new Stock("MSFT", "Microsoft", 430.20));
        addStock(new Stock("GOOG", "Alphabet", 175.80));
        addStock(new Stock("AMZN", "Amazon", 190.40));
        addStock(new Stock("TSLA", "Tesla", 250.10));
    }

    private static void addStock(Stock stock) {
        market.put(stock.getSymbol(), stock);
    }

    private static void showMarket() {
        System.out.println("\n----- MARKET DATA -----");
        System.out.printf("%-8s %-15s %12s%n", "Symbol", "Company", "Price");
        for (Stock s : market.values()) {
            System.out.printf("%-8s %-15s $%11.2f%n",
                    s.getSymbol(), s.getCompany(), s.getPrice());
        }
    }

    private static void buyStock() {
        showMarket();
        System.out.print("Enter symbol: ");
        String symbol = scanner.nextLine().trim().toUpperCase();
        Stock stock = market.get(symbol);

        if (stock == null) {
            System.out.println("Stock not found.");
            return;
        }

        System.out.print("Quantity: ");
        try {
            int qty = Integer.parseInt(scanner.nextLine());
            if (qty <= 0) throw new NumberFormatException();

            double cost = qty * stock.getPrice();
            if (cost > cash) {
                System.out.println("Insufficient cash.");
                return;
            }

            Holding holding = portfolio.get(symbol);
            if (holding == null) {
                portfolio.put(symbol, new Holding(stock, qty, cost));
            } else {
                holding.buy(qty, cost);
            }
            cash -= cost;
            System.out.printf("Bought %d shares of %s for $%.2f%n", qty, symbol, cost);
        } catch (NumberFormatException e) {
            System.out.println("Enter a valid positive integer.");
        }
    }

    private static void sellStock() {
        if (portfolio.isEmpty()) {
            System.out.println("Portfolio is empty.");
            return;
        }

        showPortfolio();
        System.out.print("Enter symbol to sell: ");
        String symbol = scanner.nextLine().trim().toUpperCase();
        Holding holding = portfolio.get(symbol);

        if (holding == null) {
            System.out.println("You do not own this stock.");
            return;
        }

        System.out.print("Quantity: ");
        try {
            int qty = Integer.parseInt(scanner.nextLine());
            if (qty <= 0 || qty > holding.getQuantity()) {
                System.out.println("Invalid quantity.");
                return;
            }

            double proceeds = qty * holding.getStock().getPrice();
            holding.sell(qty, proceeds);
            cash += proceeds;

            if (holding.getQuantity() == 0) portfolio.remove(symbol);
            System.out.printf("Sold %d shares of %s for $%.2f%n", qty, symbol, proceeds);
        } catch (NumberFormatException e) {
            System.out.println("Enter a valid quantity.");
        }
    }

    private static void showPortfolio() {
        System.out.println("\n----- PORTFOLIO -----");
        System.out.printf("Available cash: $%.2f%n", cash);

        if (portfolio.isEmpty()) {
            System.out.println("No holdings.");
            return;
        }

        double totalInvested = 0;
        double currentValue = 0;

        System.out.printf("%-8s %-10s %-15s %-15s%n",
                "Symbol", "Shares", "Invested", "Current Value");

        for (Holding h : portfolio.values()) {
            double value = h.getQuantity() * h.getStock().getPrice();
            totalInvested += h.getInvestedAmount();
            currentValue += value;
            System.out.printf("%-8s %-10d $%-14.2f $%-14.2f%n",
                    h.getStock().getSymbol(), h.getQuantity(),
                    h.getInvestedAmount(), value);
        }

        double profitLoss = currentValue - totalInvested;
        System.out.printf("Total invested: $%.2f%n", totalInvested);
        System.out.printf("Current value : $%.2f%n", currentValue);
        System.out.printf("P/L           : $%.2f%n", profitLoss);
    }

    private static void savePortfolio() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("portfolio.txt"))) {
            writer.println("Cash=" + cash);
            for (Holding h : portfolio.values()) {
                writer.printf("%s,%d,%.2f%n",
                        h.getStock().getSymbol(),
                        h.getQuantity(),
                        h.getInvestedAmount());
            }
            System.out.println("Portfolio saved to portfolio.txt");
        } catch (IOException e) {
            System.out.println("Could not save portfolio: " + e.getMessage());
        }
    }
}
