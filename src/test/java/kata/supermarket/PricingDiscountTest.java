package kata.supermarket;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class PricingDiscountTest {

    @DisplayName("Add pricing discount schema")
    @MethodSource("pricingDiscountsFactory")
    @ParameterizedTest(name = "{0}")
    void pricingDiscounts(String description, Discount expectedDiscount, List<Discount> discounts) {
        final PricingDiscount pricingDiscount = PricingDiscount.empty();

        discounts.forEach(pricingDiscount::add);

        assertEquals(discounts.size(), pricingDiscount.total());
    }

    @DisplayName("Half price of discount per kilo")
    @MethodSource("vegetablesItemsFactory")
    @ParameterizedTest(name = "WeightInKilos: {0} PricePerKilo: {1} ItemName: {2} ExpedtedPrice: {3}")
    void applyDiscountEachKiloOfVegatablesForHalfPrice(String weightInKilos, String pricePerKilo, String itemName, String expectedPrice) {
        final List<Item> items = Arrays.asList(itemByWeight(weightInKilos, pricePerKilo, itemName));
        final PricingDiscount pricingDiscount =
            PricingDiscount.of(new BuyOneKiloOfVegetablesForHalfPriceDiscount(
                items.stream().map(Item::itemName).collect(Collectors.toList())
            ));

        Map<Item, BigDecimal> discounts = pricingDiscount.calculate(items);

        assertEquals(1, discounts.size());
        assertEquals(new BigDecimal(expectedPrice), discounts.get(items.get(0)));
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

    static Stream<Arguments> pricingDiscountsFactory() {
        return Stream.of(
            noDiscount(),
            buyOneItemGetOneFreeDiscount()
        );
    }

    static Stream<Arguments> vegetablesItemsFactory() {
        return Stream.of(
            Arguments.of("3.50", "4.99", "Courgettes", "10.00"),
            Arguments.of("2.75", "3.25", "Celery", "5.70"),
            Arguments.of("1.00", "4.99", "Onions", "2.50"));
    }

    private static Item aPintOfMilk() {
        return new Product(new BigDecimal("0.51")).oneOf(new ItemName("Pint of Milk"));
    }

    private static Item oneKiloGramsOfOnions() {
        return aKiloOfVegetables().weighing(new BigDecimal("1.00"), new ItemName("Onios"));
    }

    private static Item threeAndHalfKiloGramsOfCourgettes() {
        return aKiloOfVegetables().weighing(new BigDecimal("3.50"), new ItemName("Courgettes"));
    }

    private static WeighedProduct aKiloOfVegetables() {

        return new WeighedProduct(new BigDecimal("4.99"));
    }

    private static Item itemByWeight(final String weightInKilos, final String pricePerKilo, final String itemName) {
        return
            new WeighedProduct(new BigDecimal(pricePerKilo))
                .weighing(new BigDecimal(weightInKilos), new ItemName(itemName));
    }
    private static Arguments buyOneItemGetOneFreeDiscount() {
        return Arguments.of("Buy one item, get one free", new BuyOneItemGetOneFreeDiscount(), Collections.singletonList(new BuyOneItemGetOneFreeDiscount()));
    }

    private static Arguments noDiscount() {
        return Arguments.of("No discount", new NoDiscount(), Collections.emptyList());
    }
}
