import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class MaintenanceDTO {
    private Long id;
    private Long assetId;
    private LocalDate scheduledDate;
    private String taskDescription;
    private String assignedTechnician;
    private String status;
    private BigDecimal cost;
    private String notes;
} 