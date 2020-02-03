package kata.supermarket;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class NoDiscount implements Discount {
    public static final BigDecimal ZERO_DISCOUNT = BigDecimal.valueOf(0, 2);

    @Override
    public Map<Item, BigDecimal> calculate(List<Item> items) {
        return items.stream().collect(Collectors.toMap(Function.identity(), item -> ZERO_DISCOUNT));
    }
}
