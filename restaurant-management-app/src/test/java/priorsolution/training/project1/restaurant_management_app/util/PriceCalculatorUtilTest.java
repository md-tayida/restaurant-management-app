package priorsolution.training.project1.restaurant_management_app.util;


import org.junit.jupiter.api.Test;
import priorsolution.training.project1.restaurant_management_app.entity.OrderItemEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
class PriceCalculatorUtilTest {

    // เทสกรณีปกติ: เมื่อมีรายการสินค้าแต่ละรายการมีราคาครบ
    // ควรได้ผลรวมราคาที่ถูกต้อง
    @Test
    void calculateTotalPrice_whenAllItemsHavePrice_shouldReturnCorrectTotal() {
        OrderItemEntity item1 = new OrderItemEntity();
        item1.setPrice(new BigDecimal("100.50"));

        OrderItemEntity item2 = new OrderItemEntity();
        item2.setPrice(new BigDecimal("49.50"));

        BigDecimal total = PriceCalculatorUtil.calculateTotalPrice(List.of(item1, item2));

        assertThat(total).isEqualByComparingTo(new BigDecimal("150.00"));
    }

    // เทสกรณี: หากรายการสินค้ามีราคาที่เป็น null
    // ระบบควรถือว่า null = 0 และคำนวณผลรวมได้ถูกต้อง
    @Test
    void calculateTotalPrice_whenItemHasNullPrice_shouldTreatAsZero() {
        OrderItemEntity item1 = new OrderItemEntity();
        item1.setPrice(null); // ราคาว่าง/null

        OrderItemEntity item2 = new OrderItemEntity();
        item2.setPrice(new BigDecimal("50"));

        BigDecimal total = PriceCalculatorUtil.calculateTotalPrice(List.of(item1, item2));

        assertThat(total).isEqualByComparingTo(new BigDecimal("50"));
    }

    // เทสกรณี: หากรายการสินค้าว่างเปล่า (empty list)
    // ควรคืนค่าผลรวมเป็น 0
    @Test
    void calculateTotalPrice_whenItemListIsEmpty_shouldReturnZero() {
        BigDecimal total = PriceCalculatorUtil.calculateTotalPrice(List.of());
        assertThat(total).isEqualByComparingTo(BigDecimal.ZERO);
    }

    // เทสกรณี: หากส่ง null แทนรายการสินค้า
    // เมธอดควรจัดการได้และคืนค่าผลรวมเป็น 0 แทนที่จะ throw exception
    @Test
    void calculateTotalPrice_whenItemListIsNull_shouldReturnZero() {
        BigDecimal total = PriceCalculatorUtil.calculateTotalPrice(null);
        assertThat(total).isEqualByComparingTo(BigDecimal.ZERO);
    }
}
