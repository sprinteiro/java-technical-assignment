package kata.supermarket;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface Discount {
    Map<Item, BigDecimal> calculate(List<Item> items);
}
