package priorsolution.training.project1.restaurant_management_app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import priorsolution.training.project1.restaurant_management_app.entity.enums.TableStatusEnum;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TableInfoDTO {

    private Long id;
    @NotNull(message = "Table NO must not be null")
    @NotBlank(message = "Table name is required")
    @Size(max = 50, message = "Table name must be less than 50 characters")
    private String tableNumber;
    @NotNull(message = "Table status must not be null")
    private TableStatusEnum status;
   // private String qrCode; // สมมุติว่าเก็บเป็น URL

   // private List<OrderDTO> orders;
}