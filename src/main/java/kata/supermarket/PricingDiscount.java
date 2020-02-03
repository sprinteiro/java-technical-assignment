package kata.supermarket;

import java.math.BigDecimal;
import java.util.*;

public class PricingDiscount {

    private final List<Discount> discounts = new ArrayList<>();

    private PricingDiscount() {
    }

    public static PricingDiscount of(Discount... discounts) {
        PricingDiscount pricingDiscount = new PricingDiscount();
        Arrays.stream(discounts).forEach(pricingDiscount::add);

        return pricingDiscount;
    }

    public static PricingDiscount empty() {
        return new PricingDiscount();
    }

    public void add(final Discount discount) {
        this.discounts.add(discount);
    }

    public int total() {
        return this.discounts.size();
    }

    public Map<Item, BigDecimal> calculate(List<Item> items) {
        // TODO: Choose discount strategy and delegate to calculate discount
        Map<Item, BigDecimal> priceDiscount = new HashMap<>();

        for (Discount discount: discounts) {
            priceDiscount.putAll(discount.calculate(items));
        }

        return priceDiscount;
    }
}
