public class App  {
    private String id;
    private String name;
    private int amount;
    private int price;

    public App (String id, String name, int amount, int price) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.price = price;
    }

    // getters / setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }
    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }
}
