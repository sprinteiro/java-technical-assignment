package kata.supermarket;

import java.math.BigDecimal;

public class Product {

    private final BigDecimal pricePerUnit;

    public Product(final BigDecimal pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    BigDecimal pricePerUnit() {
        return pricePerUnit;
    }

    public Item oneOf(final ItemName itemName) {
        return new ItemByUnit(this, itemName);
    }
}
