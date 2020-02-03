package kata.supermarket;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

public class ItemByWeight implements Item {

    private final WeighedProduct product;
    private final BigDecimal weightInKilos;
    private UUID id;
    private final ItemName itemName;

    ItemByWeight(final WeighedProduct product, final BigDecimal weightInKilos, final ItemName itemName) {
        this.product = product;
        this.weightInKilos = weightInKilos;
        this.id = UUID.randomUUID();
        this.itemName = itemName;
    }

    public BigDecimal price() {
        return product.pricePerKilo().multiply(weightInKilos).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public UUID id() {
        return id;
    }

    @Override
    public ItemName itemName() {
        return itemName;
    }
}
