package kata.supermarket;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BuyThreeItemsForThePriceOfTwoDiscount implements Discount {
    @Override
    public Map<Item, BigDecimal> calculate(List<Item> items) {
        Map<ItemName, List<Item>> itemsByName = items.stream().collect(Collectors.groupingBy(Item::itemName));

        Map<Item, BigDecimal> itemDiscountPriceMap = new HashMap<>();

        itemsByName.forEach((name, itemList) -> {
            final boolean isThreeItems = itemList.size() == 3;

            if (isThreeItems) {
                BigDecimal priceWithDiscount = itemList.get(0).price();

                itemDiscountPriceMap.put(itemList.get(0), BigDecimal.valueOf(0, 2));
                itemDiscountPriceMap.put(itemList.get(1), BigDecimal.valueOf(0, 2));
                itemDiscountPriceMap.put(itemList.get(2), priceWithDiscount);
            }
        });

        return itemDiscountPriceMap;
    }
}
