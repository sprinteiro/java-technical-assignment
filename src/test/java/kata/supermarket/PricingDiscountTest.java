package kata.supermarket;

import org.junit.jupiter.api.DisplayName;
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
import static org.junit.jupiter.api.Assertions.assertTrue;


public class PricingDiscountTest {

    private static final BigDecimal ZERO = BigDecimal.valueOf(0, 2);

    @DisplayName("Add pricing discount schema")
    @MethodSource("pricingDiscountsFactory")
    @ParameterizedTest(name = "{0}")
    void pricingDiscounts(String description, Discount expectedDiscount, List<Discount> discounts) {
        final PricingDiscount pricingDiscount = PricingDiscount.empty();

        discounts.forEach(pricingDiscount::add);

        assertEquals(discounts.size(), pricingDiscount.total());
    }

    @DisplayName("Per weight - Apply discount to item weight, half price of discount per kilo")
    @MethodSource("vegetablesItemsFactory")
    @ParameterizedTest(name = "WeightInKilos: {0} PricePerKilo: {1} ItemName: {2} ExpedtedPrice: {3}")
    void applyDiscountEachKiloOfVegatablesForHalfPrice(String weightInKilos, String pricePerKilo, String itemName, String expectedPrice) {
        final List<Item> items = Collections.singletonList((itemByWeight(weightInKilos, pricePerKilo, itemName)));
        final PricingDiscount pricingDiscount =
            PricingDiscount.of(new BuyOneKiloOfVegetablesForHalfPriceDiscount(
                items.stream().map(Item::itemName).collect(Collectors.toList())
            ));

        Map<Item, BigDecimal> discounts = pricingDiscount.calculate(items);

        assertEquals(1, discounts.size());
        assertEquals(new BigDecimal(expectedPrice), discounts.get(items.get(0)));
    }

    @DisplayName("Per unit - Apply discount to items units")
    @MethodSource("perUnitItemsFactory")
    @ParameterizedTest(name = "{0} - ExpedtedPrice: {3}")
    void applyDiscountPerUnitsOfItems(final String description, final Discount discount, final List<Item> items, final List<BigDecimal> expectedPrice) {
        final PricingDiscount pricingDiscount = PricingDiscount.of(discount);

        Map<Item, BigDecimal> discounts = pricingDiscount.calculate(items);

        assertTrue(items.size() == discounts.size());

        for (int i=0; i<items.size(); i++) {
            assertEquals(expectedPrice.get(i), discounts.get(items.get(i)));
        }

    }
    private static Arguments lineOf(final String description, final Discount discount, final List<Item> items, final List<BigDecimal> prices) {
        return Arguments.of(
            description,
            discount,
            items,
            prices);
    }
    static Stream<Arguments> perUnitItemsFactory() {
        final Item pintOfMilk = aPintOfMilk();
        final BigDecimal expectedPriceWithDiscountOfThreeForThePriceOfTwoDiscount = aPintOfMilk().price().multiply(new BigDecimal(2)).divide(new BigDecimal(3), RoundingMode.HALF_UP);
        final BigDecimal expectedPriceWithDiscountOfTwoForOnePound = new BigDecimal("0.01");
        return Stream.of(
           lineOf("No discount",
                new NoDiscount(),
                Collections.singletonList(pintOfMilk),
                Collections.singletonList(ZERO)),
            lineOf("Buy one, get one free",
                new BuyOneItemGetOneFreeDiscount(),
                Arrays.asList(aPintOfMilk(), aPintOfMilk()),
                Arrays.asList(ZERO, pintOfMilk.price())),
            lineOf("Buy three for the price of two",
                new BuyThreeItemsForThePriceOfTwoDiscount(),
                Arrays.asList(aPintOfMilk(),aPintOfMilk(), aPintOfMilk()),
                Arrays.asList(BigDecimal.valueOf(0, 2), BigDecimal.valueOf(0, 2), pintOfMilk.price())),
            lineOf("Buy two items by one pound",
                new BuyTwoItemsByOnePoundDiscount(),
                Arrays.asList(aPintOfMilk(),aPintOfMilk()),
                Arrays.asList(expectedPriceWithDiscountOfTwoForOnePound, expectedPriceWithDiscountOfTwoForOnePound))
            );
    }

    static Stream<Arguments> pricingDiscountsFactory() {
        return Stream.of(
            noDiscount(),
            buyOneItemGetOneFreeDiscount()
        );
    }

    static Stream<Arguments> vegetablesItemsFactory() {
        return Stream.of(
            Arguments.of("3.50", "4.99", "Courgettes", "7.50"),
            Arguments.of("2.75", "3.25", "Celery", "3.26"),
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
