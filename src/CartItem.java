package src;

public class CartItem {
    private int userId;
    private int itemId;
    private int quantity;
    private String specialInstructions;

    public CartItem(int userId, int itemId, int quantity, String specialInstructions) {
        this.userId = userId;
        this.itemId = itemId;
        this.quantity = quantity;
        this.specialInstructions = specialInstructions;
    }

    // Constructor without special instructions
    public CartItem(int userId, int itemId, int quantity) {
        this(userId, itemId, quantity, "");
    }

    // Getters
    public int getUserId() { return userId; }
    public int getItemId() { return itemId; }
    public int getQuantity() { return quantity; }
    public String getSpecialInstructions() { return specialInstructions; }

    // Setters
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setSpecialInstructions(String instructions) { this.specialInstructions = instructions; }
} 