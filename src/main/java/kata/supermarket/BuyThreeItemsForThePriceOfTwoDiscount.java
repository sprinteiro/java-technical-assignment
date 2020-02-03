package kata.supermarket;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
                // Apply discount, three items for the price of two so that
                // the item's price with discount is (item's price * 2) / 3
                BigDecimal priceWithDiscount = itemList.get(0).price()
                    .multiply(new BigDecimal(2)).divide(new BigDecimal(3), RoundingMode.HALF_UP);

                itemDiscountPriceMap.put(itemList.get(0), priceWithDiscount);
                itemDiscountPriceMap.put(itemList.get(1), priceWithDiscount);
                itemDiscountPriceMap.put(itemList.get(2), priceWithDiscount);
            }
        });

        return itemDiscountPriceMap;
    }
}
