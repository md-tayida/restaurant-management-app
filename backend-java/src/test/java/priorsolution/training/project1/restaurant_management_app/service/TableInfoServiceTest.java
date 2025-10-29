package priorsolution.training.project1.restaurant_management_app.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import priorsolution.training.project1.restaurant_management_app.Response.TableInfoResponse;
import priorsolution.training.project1.restaurant_management_app.entity.TableInfoEntity;
import priorsolution.training.project1.restaurant_management_app.entity.enums.TableStatusEnum;
import priorsolution.training.project1.restaurant_management_app.exception.ResourceNotFoundException;
import priorsolution.training.project1.restaurant_management_app.repository.TableInfoRepository;
import priorsolution.training.project1.restaurant_management_app.request.TableInfoRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TableInfoServiceTest {

    @Mock
    private TableInfoRepository repository;

    @InjectMocks
    private TableInfoService service;

    private TableInfoEntity tableEntity;
    private TableInfoRequest tableRequest;

    @BeforeEach
    void setUp() {
        tableEntity = new TableInfoEntity();
        tableEntity.setId(1L);
        tableEntity.setTableNumber(5L);
        tableEntity.setStatus(TableStatusEnum.AVAILABLE);

        tableRequest = new TableInfoRequest();
        tableRequest.setTableNumber(5L);
        tableRequest.setStatus(TableStatusEnum.OCCUPIED);
    }

    @Test
    void getAllTables_ShouldReturnList() {
        when(repository.findAllByOrderByTableNumberAsc())
                .thenReturn(List.of(tableEntity));

        List<TableInfoResponse> result = service.getAllTables();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        verify(repository, times(1)).findAllByOrderByTableNumberAsc();
    }

    @Test
    void getAllTables_WhenEmpty_ShouldThrow() {
        when(repository.findAllByOrderByTableNumberAsc()).thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class, () -> service.getAllTables());
        verify(repository, times(1)).findAllByOrderByTableNumberAsc();
    }

    @Test
    void getTableById_ShouldReturnTable() {
        when(repository.findById(1L)).thenReturn(Optional.of(tableEntity));

        TableInfoResponse response = service.getTableById(1L);

        assertThat(response.getId()).isEqualTo(1L);
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void getTableById_WhenNotFound_ShouldThrow() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getTableById(1L));
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void createTable_ShouldReturnCreatedTable() {
        when(repository.save(any(TableInfoEntity.class))).thenReturn(tableEntity);

        TableInfoResponse response = service.createTable(tableRequest);

        assertThat(response.getId()).isEqualTo(1L);
        verify(repository, times(1)).save(any(TableInfoEntity.class));
    }

    @Test
    void deleteTable_ShouldCallRepositoryDelete() {
        when(repository.findById(1L)).thenReturn(Optional.of(tableEntity));

        service.deleteTable(1L);

        verify(repository, times(1)).delete(tableEntity);
    }

    @Test
    void deleteTable_WhenNotFound_ShouldThrow() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.deleteTable(1L));
        verify(repository, never()).delete(any());
    }

    @Test
    void updateTableStatus_ShouldUpdateAndReturn() {
        when(repository.findById(1L)).thenReturn(Optional.of(tableEntity));
        when(repository.save(any(TableInfoEntity.class))).thenReturn(tableEntity);

        TableInfoResponse response = service.updateTableStatus(1L, tableRequest);

        assertThat(response.getStatus()).isEqualTo(TableStatusEnum.OCCUPIED);
        verify(repository, times(1)).save(tableEntity);
    }

    @Test
    void updateTableStatus_WhenNotFound_ShouldThrow() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.updateTableStatus(1L, tableRequest));
    }
}
