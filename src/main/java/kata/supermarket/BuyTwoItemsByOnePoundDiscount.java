package kata.supermarket;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BuyTwoItemsByOnePoundDiscount implements Discount {
    private static final BigDecimal ZERO_DISCOUNT = BigDecimal.valueOf(0, 2);

    @Override
    public Map<Item, BigDecimal> calculate(List<Item> items) {
        Map<ItemName, List<Item>> itemsByName = items.stream().collect(Collectors.groupingBy(Item::itemName));

        Map<Item, BigDecimal> itemDiscountPriceMap = new HashMap<>();

        itemsByName.forEach((name, itemList) -> {
            itemDiscountPriceMap.put(itemList.get(0), ZERO_DISCOUNT);

            final boolean isTwoItems = itemList.size() == 2;
            final boolean isPriceGreaterThanOnePound = isTwoItems &&
                itemList.get(0).price().add(itemList.get(1).price()).compareTo(BigDecimal.ONE) > 0;

            if (isTwoItems && isPriceGreaterThanOnePound) {
                BigDecimal priceBothItems = itemList.get(0).price().add(itemList.get(1).price());
                BigDecimal priceToDiscount = priceBothItems.subtract(BigDecimal.valueOf(1));
                BigDecimal priceWithDiscountPerEachItem = priceToDiscount.divide(new BigDecimal("2"), RoundingMode.HALF_UP);

                itemDiscountPriceMap.put(itemList.get(0), priceWithDiscountPerEachItem);
                itemDiscountPriceMap.put(itemList.get(1), priceWithDiscountPerEachItem);
            }
        });

        return itemDiscountPriceMap;
    }
}
