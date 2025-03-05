/**
 * The {@code ProductEntry} class represents a product with an ID, name, and price.
 * It is used to store product details retrieved from the database.
 * 
 * This class provides:
 * - A constructor for initializing product attributes.
 * - Public fields to store product data.
 * 
 * @author Luke Conran
 * @author Kamryn Vogel
 * @author Christian Fadal
 * @author Macsen Casaus
 * @author Surada Suwansathit
 */
public class ProductEntry {
    /**
     * The unique identifier for the product.
     */
    public int id;

    /**
     * The name of the product.
     */
    public String name;

    /**
     * The price of the product in dollars.
     */
    public double price;

    /**
     * Constructs a new {@code ProductEntry} with the given ID, name, and price.
     * 
     * @param id    The unique product ID.
     * @param name  The name of the product.
     * @param price The price of the product in dollars.
     */
    public ProductEntry(int id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }
}
