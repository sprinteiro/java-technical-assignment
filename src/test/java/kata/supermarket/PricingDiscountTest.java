package kata.supermarket;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class PricingDiscountTest {

    @DisplayName("Add pricing discount schema")
    @MethodSource
    @ParameterizedTest(name = "{0}")
    void pricingDiscounts(String description, Discount expectedDiscount, List<Discount> discounts) {
        final PricingDiscount pricingDiscount = PricingDiscount.empty();

        discounts.forEach(pricingDiscount::add);

        assertEquals(discounts.size(), pricingDiscount.total());
    }

    @Test
    void buyOneWithoutDiscount() {
        final PricingDiscount pricingDiscount = PricingDiscount.of(new NoDiscount());
        final List<Item> items = Arrays.asList(aPintOfMilk());
        final BigDecimal expectedDiscountPrice = new BigDecimal("0.0");

        Map<Item, BigDecimal> discounts = pricingDiscount.calculate(items);

        assertEquals(1, discounts.size());
        assertEquals(expectedDiscountPrice, discounts.get(items.get(0)));
    }

    @Test
    void applyDiscountBuyOneGetOneFree() {
        final PricingDiscount pricingDiscount = PricingDiscount.of(new BuyOneItemGetOneFreeDiscount());
        final List<Item> items = Arrays.asList(aPintOfMilk(),aPintOfMilk());
        final BigDecimal noDiscount = new BigDecimal("0.0");

        Map<Item, BigDecimal> discounts = pricingDiscount.calculate(items);

        assertEquals(2, discounts.size());
        assertEquals(noDiscount, discounts.get(items.get(0)));
        assertEquals(items.get(1).price(), discounts.get(items.get(1)));
    }

    @Test
    void applyDiscountBuyThreeItemsForThePriceOfTwo() {
        final PricingDiscount pricingDiscount = PricingDiscount.of(new BuyThreeItemsForThePriceOfTwoDiscount());
        final List<Item> items = Arrays.asList(aPintOfMilk(),aPintOfMilk(), aPintOfMilk());
        final BigDecimal expectedPpriceWithDiscount = aPintOfMilk().price().multiply(new BigDecimal(2)).divide(new BigDecimal(3), RoundingMode.HALF_UP);

        Map<Item, BigDecimal> discounts = pricingDiscount.calculate(items);

        assertEquals(3, discounts.size());
        items.forEach(item -> assertEquals(expectedPpriceWithDiscount, discounts.get(item)));
    }
    @Test
    void applyDiscountBuyTwoItemsByOnePound() {
        final PricingDiscount pricingDiscount = PricingDiscount.of(new BuyTwoItemsByOnePoundDiscount());
        final List<Item> items = Arrays.asList(aPintOfMilk(),aPintOfMilk());

        Map<Item, BigDecimal> discounts = pricingDiscount.calculate(items);

        assertEquals(2, discounts.size());
        assertEquals(items.get(0).price().subtract(new BigDecimal("1").divide(new BigDecimal("2"), RoundingMode.HALF_UP)),
            discounts.get(items.get(0)));
        assertEquals(items.get(0).price().subtract(new BigDecimal("1").divide(new BigDecimal("2"), RoundingMode.HALF_UP)),
            discounts.get(items.get(1)));
    }

    private static Item aPintOfMilk() {
        return new Product(new BigDecimal("0.51")).oneOf(new ItemName("Pint of Milk"));
    }

    static Stream<Arguments> pricingDiscounts() {
        return Stream.of(
            noDiscount(),
            buyOneItemGetOneFreeDiscount()
        );
    }

    private static Arguments buyOneItemGetOneFreeDiscount() {
        return Arguments.of("Buy one item, get one free", new BuyOneItemGetOneFreeDiscount(), Collections.singletonList(new BuyOneItemGetOneFreeDiscount()));
    }

    private static Arguments noDiscount() {
        return Arguments.of("No discount", new NoDiscount(), Collections.emptyList());
    }
}
