package kata.supermarket;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

public class BuyOneKiloOfVegetablesForHalfPriceDiscount implements Discount {
    private static final BigDecimal ONE_KILO = new BigDecimal("1");
    private final List<ItemName> itemNames;

    public BuyOneKiloOfVegetablesForHalfPriceDiscount(final List<ItemName> itemNames) {
        this.itemNames = itemNames;
    }

    @Override
    public Map<Item, BigDecimal> calculate(List<Item> items) {
        Map<ItemName, List<Item>> itemsByName = items.stream().collect(Collectors.groupingBy(Item::itemName));

        Map<Item, BigDecimal> itemDiscountPriceMap = new HashMap<>();
        for (Map.Entry<ItemName, List<Item>> entry: itemsByName.entrySet()) {
            List<Item> vegetableItems =
                entry.getValue().stream()
                .filter(item -> itemNames.contains(item.itemName()))
                .collect(Collectors.toList());

            Map<Item, BigDecimal> vegetableItemDiscountPriceMap =
                vegetableItems.stream()
                    .map(vegetableItem -> {
                        ItemByWeight itemByWeight = ((ItemByWeight) vegetableItem);
                        BigDecimal[] kilosToApplyDiscount = itemByWeight.weightInKilos().divideAndRemainder(ONE_KILO);
                        BigDecimal priceWithDiscountOfHalfPricePerKilos =
                            kilosToApplyDiscount[0].multiply(
                                itemByWeight.product().pricePerKilo().divide(new BigDecimal(2), RoundingMode.HALF_UP));
                        // Rest of weight (less than one kilo) no discount is applied
                        BigDecimal priceWithoutDiscount =
                            kilosToApplyDiscount[1].multiply(itemByWeight.product().pricePerKilo());

                        return new AbstractMap.SimpleImmutableEntry<>(
                            itemByWeight,
                            priceWithDiscountOfHalfPricePerKilos.add(priceWithoutDiscount));
                    }).collect(Collectors.toMap(Map.Entry::getKey, e ->  e.getValue().setScale(2, RoundingMode.HALF_UP)));

            itemDiscountPriceMap.putAll(vegetableItemDiscountPriceMap);
        }

        return itemDiscountPriceMap;
    }
}
