package priorsolution.training.project1.restaurant_management_app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import priorsolution.training.project1.restaurant_management_app.Response.TableInfoResponse;

import priorsolution.training.project1.restaurant_management_app.entity.TableInfoEntity;
import priorsolution.training.project1.restaurant_management_app.exception.ResourceNotFoundException;
import priorsolution.training.project1.restaurant_management_app.mapper.TableInfoMapper;
import priorsolution.training.project1.restaurant_management_app.repository.TableInfoRepository;
import priorsolution.training.project1.restaurant_management_app.request.TableInfoRequest;

import java.util.List;
@Service
@RequiredArgsConstructor
public class TableInfoService {

    private final TableInfoRepository repository;

    public List<TableInfoResponse> getAllTables() {
        List<TableInfoEntity> entities = repository.findAllByOrderByTableNumberAsc();
        if (entities.isEmpty()) {
            throw new ResourceNotFoundException("No tables found", "TABLES_NOT_FOUND");
        }
        return entities.stream()
                .map(TableInfoMapper::toInfoResponse)
                .toList();
    }

    public TableInfoResponse getTableById(Long id) {
        TableInfoEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Table not found", "TABLE_NOT_FOUND"));
        return TableInfoMapper.toInfoResponse(entity);
    }

    public TableInfoResponse createTable(TableInfoRequest request) {
        boolean exists = repository.existsByTableNumber(request.getTableNumber());
        if (exists) {
            throw new IllegalArgumentException("Table number " + request.getTableNumber() + " already exists");
        }
        TableInfoEntity entity = TableInfoMapper.toInfoEntity(request);
        TableInfoEntity saved = repository.save(entity);

        return TableInfoMapper.toInfoResponse(saved);
    }


    public void deleteTable(Long id) {
        TableInfoEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Table not found", "TABLE_NOT_FOUND"));
        repository.delete(entity);
    }
    public TableInfoResponse updateTableStatus(Long id, TableInfoRequest request) {
        TableInfoEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Table not found", "TABLE_NOT_FOUND"));
        entity.setStatus(request.getStatus());
        TableInfoEntity saved = repository.save(entity);
        return TableInfoMapper.toInfoResponse(saved);
    }


}
