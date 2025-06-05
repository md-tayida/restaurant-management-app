package priorsolution.training.project1.restaurant_management_app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
//import org.springframework.web.server.ResponseStatusException;
import priorsolution.training.project1.restaurant_management_app.dto.MenuStatusRequestDTO;
import priorsolution.training.project1.restaurant_management_app.dto.TableInfoDTO;
import priorsolution.training.project1.restaurant_management_app.dto.TableInfoStatusRequestDTO;
import priorsolution.training.project1.restaurant_management_app.entity.MenuEntity;
import priorsolution.training.project1.restaurant_management_app.entity.TableInfoEntity;
import priorsolution.training.project1.restaurant_management_app.entity.enums.TableStatusEnum;
import priorsolution.training.project1.restaurant_management_app.exception.ResourceNotFoundException;
import priorsolution.training.project1.restaurant_management_app.mapper.TableInfoMapper;
import priorsolution.training.project1.restaurant_management_app.repository.TableInfoRepository;

import java.util.List;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class TableInfoService {

    private final TableInfoRepository tableInfoRepository;

    // Read all tables
    public List<TableInfoDTO> getAllTables() {
        List<TableInfoEntity> tables = tableInfoRepository.findAllByOrderByIdAsc();
        if (tables.isEmpty()) {
            throw new ResourceNotFoundException("No tables found", "TABLES_NOT_FOUND");
        }
        return tables.stream()
                .map(TableInfoMapper::toDTO)
                .collect(Collectors.toList());
    }

    // Read table by id
    public TableInfoDTO getTableById(Long id) {
        TableInfoEntity table = tableInfoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Table with ID " + id + " not found", "TABLE_NOT_FOUND"));
        return TableInfoMapper.toDTO(table);
    }

    // Create new table
    // Create new table
    public TableInfoDTO createTable(TableInfoDTO dto) {
        TableInfoEntity entity = TableInfoMapper.toEntity(dto);
        entity.setStatus(TableStatusEnum.AVAILABLE); // ตั้งค่า default เป็น AVAILABLE
        TableInfoEntity saved = tableInfoRepository.save(entity);
        return TableInfoMapper.toDTO(saved);
    }

    // Delete table by ID
    public void deleteTable(Long id) {
        if (!tableInfoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Table with ID " + id + " not found", "TABLE_NOT_FOUND");
        }
        tableInfoRepository.deleteById(id);
    }


    public TableInfoStatusRequestDTO updateTableStatus(Long id, TableInfoStatusRequestDTO dto) {
        if (dto.getStatus() == null) {
            throw new IllegalArgumentException("Status must not be null");
        }

        TableInfoEntity entity = tableInfoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Table not found", "TABLE_NOT_FOUND"));

        entity.setStatus(dto.getStatus());

        TableInfoEntity updated = tableInfoRepository.save(entity);
        TableInfoStatusRequestDTO response = new TableInfoStatusRequestDTO();
        response.setStatus(updated.getStatus());
        return response;
    }

    /// 5hk สร้าง order ใหม่ จะเปลี่ยนสถานะ table to OCCUPIED

    public void setTableStatus(Long id, TableStatusEnum status) {
        if (status == null) {
            throw new IllegalArgumentException("Status must not be null");
        }

        TableInfoEntity entity = tableInfoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Table not found", "TABLE_NOT_FOUND"));

        entity.setStatus(status);
        tableInfoRepository.save(entity);
    }

    /// ///////เอาไว้ ภายใน backend ใช้ร่วมกับ setTableStatus
    public TableInfoEntity getEntityById(Long id) {
        return tableInfoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Table not found", "NOT_FOUND"));
    }


}


//

