package kata.supermarket;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BuyOneItemGetOneFreeDiscount implements Discount {
    private static final BigDecimal ZERO_DISCOUNT = BigDecimal.valueOf(0, 2);

    @Override
    public Map<Item, BigDecimal> calculate(List<Item> items) {
        Map<ItemName, List<Item>> itemsByName = items.stream().collect(Collectors.groupingBy(Item::itemName));

        Map<Item, BigDecimal> itemDiscountPriceMap = new HashMap<>();

        itemsByName.forEach((name, itemList) -> {
            itemDiscountPriceMap.put(itemList.get(0), ZERO_DISCOUNT);

            if (itemList.size() == 2) {
                // Apply discount, second item is free so that the discount is the item's price
                itemDiscountPriceMap.put(itemList.get(1), new BigDecimal(itemList.get(0).price().toString()));
            }
        });

        return itemDiscountPriceMap;
    }
}
