package priorsolution.training.project1.restaurant_management_app.dto;

import jakarta.validation.constraints.NotNull;
import jdk.jshell.Snippet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import priorsolution.training.project1.restaurant_management_app.entity.enums.TableStatusEnum;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TableInfoDTO {

    private Long id;
    @NotNull(message = "Table NO must not be null")
    private String tableNumber;
    @NotNull(message = "Table status must not be null")
    private TableStatusEnum status;
   // private String qrCode; // สมมุติว่าเก็บเป็น URL

    private List<OrderDTO> orders;
}