package kata.supermarket;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BasketTest {

    private static final String PICK_AND_MIX_NAME = "Pick and Mix";
    private static final String PRICE_0_49 = "0.49";
    private static final String PRICE_0_51 = "0.51";

    @DisplayName("No Discounts - Basket provides its total value when containing...")
    @MethodSource("basketProvidesTotalValue")
    @ParameterizedTest(name = "{0}")
    void basketProvidesTotalValue(String description, String expectedTotal, Iterable<Item> items) {
        final Basket basket = new Basket();
        items.forEach(basket::add);
        assertEquals(new BigDecimal(expectedTotal), basket.total());
    }

    @DisplayName("Discounts - Basket provides its total value when containing...")
    @MethodSource("basketProvidesTotalValueWithDiscounts")
    @ParameterizedTest(name = "{0}")
    void basketProvidesTotalValueWithDiscounts(String description, String expectedTotal, Iterable<Item> items, Discount discount) {
        // TODO: DEBUG
        final Basket basket = new Basket(PricingDiscount.of(discount));
        items.forEach(basket::add);
        BigDecimal total = basket.total();
        assertEquals(new BigDecimal(expectedTotal), basket.total());
    }

    static Stream<Arguments> basketProvidesTotalValueWithDiscounts() {
        Discount buyOneGetOneFreeDiscount = new BuyOneItemGetOneFreeDiscount();
        Discount buyTwoItemsByOnePoundDiscount = new BuyTwoItemsByOnePoundDiscount();
        return Stream.of(
            noItemsWithDiscount("No items - Buy One Get One Free", buyOneGetOneFreeDiscount),
            aSingleItemPricedPerUnitWithDiscount("A single item priced per unit - Buy One Get One Free", "0.49", buyOneGetOneFreeDiscount),
            multipleSameItemsPricedPerUnitWithDiscount("Multiple same items priced per unit - Buy One Get One Free",PRICE_0_51, buyOneGetOneFreeDiscount),

            noItemsWithDiscount("No items - Buy Two by One Pound", buyTwoItemsByOnePoundDiscount),
            aSingleItemPricedPerUnitWithDiscount("A single item priced per unit - Buy Two by One Pound", "0.49", buyTwoItemsByOnePoundDiscount),
            multipleSameItemsPricedPerUnitWithDiscount("Multiple same items priced per unit -  Buy Two by One Pound","1.00", buyTwoItemsByOnePoundDiscount)

        );
    }

    static Stream<Arguments> basketProvidesTotalValue() {
        return Stream.of(
                noItems(),
                aSingleItemPricedPerUnit("0.49"),
                multipleItemsPricedPerUnit("2.04"),
                aSingleItemPricedByWeight("1.25"),
                multipleItemsPricedByWeight("1.85")
        );
    }

    private static Arguments aSingleItemPricedByWeight(String totalPrice) {
        return Arguments.of("a single weighed item", totalPrice, Collections.singleton(twoFiftyGramsOfAmericanSweets()));
    }

    private static Arguments multipleItemsPricedByWeight(String totalPrice) {
        return Arguments.of("multiple weighed items", totalPrice,
                Arrays.asList(twoFiftyGramsOfAmericanSweets(), twoHundredGramsOfPickAndMix())
        );
    }

    private static Arguments multipleItemsPricedPerUnit(String totalPrice) {
        return Arguments.of("multiple items priced per unit", totalPrice,
                Arrays.asList(aPackOfDigestives(), aPintOfMilk(PRICE_0_49)));
    }

    private static Arguments multipleSameItemsPricedPerUnitWithDiscount(String description, String totalPrice, Discount discount) {
        return Arguments.of(description, totalPrice,
            Arrays.asList(aPintOfMilk(PRICE_0_51), aPintOfMilk(PRICE_0_51)), discount);
    }


    private static Arguments aSingleItemPricedPerUnit(String totalPrice) {
        return Arguments.of("a single item priced per unit", totalPrice, Collections.singleton(aPintOfMilk(PRICE_0_49)));
    }

    private static Arguments aSingleItemPricedPerUnitWithDiscount(String description, String totalPrice, Discount discount) {
        return Arguments.of(description, totalPrice, Collections.singleton(aPintOfMilk(PRICE_0_49)), discount);
    }

    private static Arguments noItems() {
        return Arguments.of("no items", "0.00", Collections.emptyList());
    }

    private static Arguments noItemsWithDiscount(String description, Discount discount) {
        return Arguments.of(description, "0.00", Collections.emptyList(), discount);
    }

    private static Item aPintOfMilk(String price) {
        return new Product(new BigDecimal(price)).oneOf(new ItemName("Pint of Milk"));
    }

    private static Item aPackOfDigestives() {
        return new Product(new BigDecimal("1.55")).oneOf(new ItemName("Pack of Digestives"));
    }

    private static WeighedProduct aKiloOfAmericanSweets() {
        return new WeighedProduct(new BigDecimal("4.99"));
    }

    private static Item twoFiftyGramsOfAmericanSweets() {
        return aKiloOfAmericanSweets().weighing(new BigDecimal(".25"), new ItemName("American Sweets"));
    }

    private static WeighedProduct aKiloOfPickAndMix() {
        return new WeighedProduct(new BigDecimal("2.99"));
    }

    private static Item twoHundredGramsOfPickAndMix() {
        return aKiloOfPickAndMix().weighing(new BigDecimal(".2"), new ItemName(PICK_AND_MIX_NAME));
    }
}