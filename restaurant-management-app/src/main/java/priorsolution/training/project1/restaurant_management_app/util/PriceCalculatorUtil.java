package priorsolution.training.project1.restaurant_management_app.util;

import priorsolution.training.project1.restaurant_management_app.entity.OrderItemEntity;

import java.math.BigDecimal;
import java.util.List;

public class PriceCalculatorUtil {
    public static BigDecimal calculateTotalPrice(List<OrderItemEntity> items) {
        if (items == null || items.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return items.stream()
                .map(item -> item.getPrice() != null ? item.getPrice() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


}
