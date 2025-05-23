package priorsolution.training.project1.restaurant_management_app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
//import org.springframework.web.server.ResponseStatusException;
import priorsolution.training.project1.restaurant_management_app.dto.TableInfoDTO;
import priorsolution.training.project1.restaurant_management_app.entity.TableInfoEntity;
import priorsolution.training.project1.restaurant_management_app.entity.enums.TableStatusEnum;
import priorsolution.training.project1.restaurant_management_app.exception.BadRequestException;
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
        return tableInfoRepository.findAll().stream()
                .map(TableInfoMapper::toDTO)
                .collect(Collectors.toList());
    }

    // Read table by id
    public TableInfoDTO getTableById(Long id) {
        TableInfoEntity table = tableInfoRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Table not found", "NOT_FOUND"));
        return TableInfoMapper.toDTO(table);
    }

    // Create new table
    public TableInfoDTO createTable(TableInfoDTO dto) {
        TableInfoEntity entity = TableInfoMapper.toEntity(dto);
        TableInfoEntity saved = tableInfoRepository.save(entity);
        return TableInfoMapper.toDTO(saved);
    }

    // Update table status
//    public TableInfoDTO updateTableStatus(Long id, TableStatusEnum status) {
//        TableInfoEntity table = tableInfoRepository.findById(id)
//                .orElseThrow(() -> new BadRequestException("Table not found", "NOT_FOUND"));
//        table.setStatus(status);
//        TableInfoEntity updated = tableInfoRepository.save(table);
//        return TableInfoMapper.toDTO(updated);
//    }

    // Update table info (general)
//    public TableInfoDTO updateTable(Long id, TableInfoDTO dto) {
//        TableInfoEntity table = tableInfoRepository.findById(id)
//                .orElseThrow(() -> new BadRequestException("Table not found", "NOT_FOUND"));
//
//        // อัปเดตฟิลด์ที่ต้องการ เช่น tableNumber, status (ถ้าอยากให้แก้ได้)
//        table.setTableNumber(dto.getTableNumber());
//        table.setStatus(dto.getStatus());
//
//        TableInfoEntity updated = tableInfoRepository.save(table);
//        return TableInfoMapper.toDTO(updated);
//    }
//
//    // Delete table by id
//    public void deleteTable(Long id) {
//        if (!tableInfoRepository.existsById(id)) {
//              throw  new BadRequestException("Table not found", "NOT_FOUND");
//        }
//        tableInfoRepository.deleteById(id);
//    }
//
//    // Custom: Get all occupied tables
//    public List<TableInfoDTO> getOccupiedTables() {
//        return tableInfoRepository.findByStatus(TableStatusEnum.OCCUPIED)
//                .stream()
//                .map(TableInfoMapper::toDTO)
//                .collect(Collectors.toList());
//    }
//

}
