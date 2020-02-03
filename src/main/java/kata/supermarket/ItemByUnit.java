package kata.supermarket;

import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.UUID;

@EqualsAndHashCode
public class ItemByUnit implements Item {
    @EqualsAndHashCode.Exclude
    private final Product product;
    private final UUID id;
    private final ItemName itemName;

    ItemByUnit(final Product product, final ItemName itemName) {
        this.product = product;
        this.id = UUID.randomUUID();
        this.itemName = itemName;
    }

    public BigDecimal price() {
        return product.pricePerUnit();
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
