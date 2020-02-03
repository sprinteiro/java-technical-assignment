package kata.supermarket;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WeighedProductTest {

    @ParameterizedTest(name="Weight:{1}, Price: {0}, Name: {3}")
    @MethodSource
    void itemFromWeighedProductHasExpectedUnitPrice(String pricePerKilo, String weightInKilos, String expectedPrice, String name) {
        final WeighedProduct weighedProduct = new WeighedProduct(new BigDecimal(pricePerKilo));
        final Item weighedItem = weighedProduct.weighing(new BigDecimal(weightInKilos), new ItemName(name));
        assertEquals(new BigDecimal(expectedPrice), weighedItem.price());
    }

    static Stream<Arguments> itemFromWeighedProductHasExpectedUnitPrice() {
        return Stream.of(
                Arguments.of("100.00", "1.00", "100.00", "Red Pepper"),
                Arguments.of("100.00", "0.33333", "33.33", "Courgette"),
                Arguments.of("100.00", "0.33335", "33.34", "Lettuce"),
                Arguments.of("100.00", "0", "0.00", "White Onion")
        );
    }

}