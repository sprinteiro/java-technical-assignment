package kata.supermarket;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class Basket {
    private final List<Item> items = new ArrayList<>();
    private final PricingDiscount pricingDiscount;

    public Basket() {
        pricingDiscount = PricingDiscount.of(new NoDiscount());
    }

    public Basket(PricingDiscount pricingDiscount) {
        this.pricingDiscount = pricingDiscount;
    }

    public void add(final Item item) {
        this.items.add(item);
    }

    List<Item> items() {
        return Collections.unmodifiableList(items);
    }

    public BigDecimal total() {
        return new TotalCalculator().calculate();
    }

    private class TotalCalculator {
        private final List<Item> items;

        TotalCalculator() {
            this.items = items();
        }

        private BigDecimal subtotal() {
            return items.stream().map(Item::price)
                    .reduce(BigDecimal::add)
                    .orElse(BigDecimal.ZERO)
                    .setScale(2, RoundingMode.HALF_UP);
        }

        /**
         * TODO: This could be a good place to apply the results of
         *  the discount calculations.
         *  It is not likely to be the best place to do those calculations.
         *  Think about how Basket could interact with something
         *  which provides that functionality.
         */
        private BigDecimal discounts() {
            Map<Item, BigDecimal> itemDiscountsMap = pricingDiscount.calculate(items());

            AtomicReference<BigDecimal> totalDiscount = new AtomicReference<>();
            totalDiscount.set(BigDecimal.valueOf(0, 2));

            itemDiscountsMap.forEach((item, bigDecimal) ->
                totalDiscount.getAndSet(totalDiscount.get().add(bigDecimal))
            );

            return totalDiscount.get();
        }

        private BigDecimal calculate() {
            return subtotal().subtract(discounts());
        }
    }
}
