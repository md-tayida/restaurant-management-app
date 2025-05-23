package priorsolution.training.project1.restaurant_management_app.util;

import priorsolution.training.project1.restaurant_management_app.entity.OrderItemEntity;

import java.math.BigDecimal;
import java.util.List;

public class PriceCalculatorUtil {
    public static BigDecimal calculateTotalPrice(List<OrderItemEntity> items) {
        return items.stream()
                .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
