package priorsolution.training.project1.restaurant_management_app.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import priorsolution.training.project1.restaurant_management_app.dto.TableInfoDTO;
import priorsolution.training.project1.restaurant_management_app.dto.TableInfoStatusRequestDTO;
import priorsolution.training.project1.restaurant_management_app.entity.TableInfoEntity;
import priorsolution.training.project1.restaurant_management_app.entity.enums.TableStatusEnum;
import priorsolution.training.project1.restaurant_management_app.exception.ResourceNotFoundException;
import priorsolution.training.project1.restaurant_management_app.mapper.TableInfoMapper;
import priorsolution.training.project1.restaurant_management_app.repository.TableInfoRepository;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TableInfoServiceTest {

    private final TableInfoRepository tableInfoRepository = mock(TableInfoRepository.class);
    private final TableInfoService tableInfoService = new TableInfoService(tableInfoRepository);

    // ทดสอบ: มีข้อมูลโต๊ะ → ควรคืน List ของ DTO
    @Test
    void getAllTables_whenDataExists_shouldReturnList() {
        TableInfoEntity entity = new TableInfoEntity();
        entity.setId(1L);
        entity.setStatus(TableStatusEnum.AVAILABLE);

        TableInfoDTO dto = new TableInfoDTO();
        dto.setId(1L);
        dto.setStatus(TableStatusEnum.AVAILABLE);

        when(tableInfoRepository.findAllByOrderByIdAsc()).thenReturn(List.of(entity));
        try (var mocked = Mockito.mockStatic(TableInfoMapper.class)) {
            mocked.when(() -> TableInfoMapper.toDTO(entity)).thenReturn(dto);

            List<TableInfoDTO> result = tableInfoService.getAllTables();
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getId()).isEqualTo(1L);
        }
    }

    // ทดสอบ: ไม่มีข้อมูลโต๊ะ → ควรโยน ResourceNotFoundException
    @Test
    void getAllTables_whenEmpty_shouldThrowResourceNotFoundException() {
        when(tableInfoRepository.findAllByOrderByIdAsc()).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> tableInfoService.getAllTables())
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No tables found");
    }

    // ทดสอบ: ดึงโต๊ะด้วย ID ที่มีอยู่ → ต้องได้ DTO
    @Test
    void getTableById_whenIdExists_shouldReturnDTO() {
        TableInfoEntity entity = new TableInfoEntity();
        entity.setId(1L);

        TableInfoDTO dto = new TableInfoDTO();
        dto.setId(1L);

        when(tableInfoRepository.findById(1L)).thenReturn(Optional.of(entity));
        try (var mocked = Mockito.mockStatic(TableInfoMapper.class)) {
            mocked.when(() -> TableInfoMapper.toDTO(entity)).thenReturn(dto);

            TableInfoDTO result = tableInfoService.getTableById(1L);
            assertThat(result.getId()).isEqualTo(1L);
        }
    }

    // ทดสอบ: ดึงโต๊ะด้วย ID ที่ไม่มีอยู่ → ควรโยน ResourceNotFoundException
    @Test
    void getTableById_whenIdNotFound_shouldThrowResourceNotFoundException() {
        when(tableInfoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tableInfoService.getTableById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Table with ID 1 not found");
    }

    // ทดสอบ: สร้างโต๊ะใหม่ด้วยข้อมูลปกติ → ควรคืน DTO ที่สร้าง
    @Test
    void createTable_whenValidInput_shouldReturnCreatedDTO() {
        TableInfoDTO dto = new TableInfoDTO();
        TableInfoEntity entity = new TableInfoEntity();
        TableInfoEntity saved = new TableInfoEntity();
        saved.setStatus(TableStatusEnum.AVAILABLE);

        TableInfoDTO response = new TableInfoDTO();
        response.setStatus(TableStatusEnum.AVAILABLE);

        try (var mocked = Mockito.mockStatic(TableInfoMapper.class)) {
            mocked.when(() -> TableInfoMapper.toEntity(dto)).thenReturn(entity);
            mocked.when(() -> TableInfoMapper.toDTO(saved)).thenReturn(response);

            when(tableInfoRepository.save(entity)).thenReturn(saved);

            TableInfoDTO result = tableInfoService.createTable(dto);
            assertThat(result.getStatus()).isEqualTo(TableStatusEnum.AVAILABLE);
        }
    }

    // ทดสอบ: สร้างโต๊ะโดยไม่กำหนด status → ต้อง default เป็น AVAILABLE
    @Test
    void createTable_whenStatusIsNull_shouldStillCreateWithDefaultStatus() {
        TableInfoDTO inputDto = new TableInfoDTO(); // ไม่กำหนด status
        TableInfoEntity entity = new TableInfoEntity();
        TableInfoEntity savedEntity = new TableInfoEntity();
        savedEntity.setStatus(TableStatusEnum.AVAILABLE);

        TableInfoDTO resultDto = new TableInfoDTO();
        resultDto.setStatus(TableStatusEnum.AVAILABLE);

        try (var mocked = Mockito.mockStatic(TableInfoMapper.class)) {
            mocked.when(() -> TableInfoMapper.toEntity(inputDto)).thenReturn(entity);
            mocked.when(() -> TableInfoMapper.toDTO(savedEntity)).thenReturn(resultDto);

            when(tableInfoRepository.save(entity)).thenReturn(savedEntity);

            TableInfoDTO result = tableInfoService.createTable(inputDto);
            assertThat(result.getStatus()).isEqualTo(TableStatusEnum.AVAILABLE);
        }
    }

    // ทดสอบ: อัปเดตสถานะโต๊ะ แต่ status = null → ควรโยน IllegalArgumentException
    @Test
    void updateTableStatus_whenStatusIsNull_shouldThrowIllegalArgumentException() {
        TableInfoEntity entity = new TableInfoEntity();
        entity.setId(1L);
        entity.setStatus(TableStatusEnum.AVAILABLE);

        when(tableInfoRepository.findById(1L)).thenReturn(Optional.of(entity));

        TableInfoStatusRequestDTO dto = new TableInfoStatusRequestDTO();
        dto.setStatus(null);

        assertThatThrownBy(() -> tableInfoService.updateTableStatus(1L, dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Status must not be null");
    }

    // ทดสอบ: อัปเดตสถานะสำเร็จ → ควร save ด้วยสถานะใหม่
    @Test
    void updateTableStatus_whenValid_shouldUpdateAndSaveEntity() {
        TableInfoEntity entity = new TableInfoEntity();
        entity.setId(1L);
        entity.setStatus(TableStatusEnum.AVAILABLE);

        when(tableInfoRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(tableInfoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        TableInfoStatusRequestDTO dto = new TableInfoStatusRequestDTO();
        dto.setStatus(TableStatusEnum.OCCUPIED);

        tableInfoService.updateTableStatus(1L, dto);

        verify(tableInfoRepository).save(argThat(saved -> saved.getStatus() == TableStatusEnum.OCCUPIED));
    }

    // ทดสอบ: อัปเดตสถานะโต๊ะแล้วต้อง return DTO ที่ถูกอัปเดต
    @Test
    void updateTableStatus_whenIdExists_shouldReturnUpdatedStatus() {
        TableInfoEntity entity = new TableInfoEntity();
        entity.setId(1L);
        entity.setStatus(TableStatusEnum.AVAILABLE);

        when(tableInfoRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(tableInfoRepository.save(any(TableInfoEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        TableInfoStatusRequestDTO requestDTO = new TableInfoStatusRequestDTO();
        requestDTO.setStatus(TableStatusEnum.OCCUPIED);

        TableInfoStatusRequestDTO result = tableInfoService.updateTableStatus(1L, requestDTO);

        assertThat(result.getStatus()).isEqualTo(TableStatusEnum.OCCUPIED);
        assertThat(entity.getStatus()).isEqualTo(TableStatusEnum.OCCUPIED);
    }

    // ทดสอบ: อัปเดตสถานะด้วย ID ที่ไม่เจอ → ควรโยน ResourceNotFoundException
    @Test
    void updateTableStatus_whenIdNotFound_shouldThrowResourceNotFoundException() {
        when(tableInfoRepository.findById(1L)).thenReturn(Optional.empty());

        TableInfoStatusRequestDTO dto = new TableInfoStatusRequestDTO();
        dto.setStatus(TableStatusEnum.OCCUPIED);

        assertThatThrownBy(() -> tableInfoService.updateTableStatus(1L, dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Table not found");
    }

    // ทดสอบ: setTableStatus โดย status = null → ควรโยน IllegalArgumentException
    @Test
    void setTableStatus_whenStatusIsNull_shouldThrowIllegalArgumentException() {
        TableInfoEntity entity = new TableInfoEntity();
        entity.setId(1L);
        entity.setStatus(TableStatusEnum.AVAILABLE);

        when(tableInfoRepository.findById(1L)).thenReturn(Optional.of(entity));

        assertThatThrownBy(() -> tableInfoService.setTableStatus(1L, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Status must not be null");
    }

    // ทดสอบ: setTableStatus สำเร็จ → ต้องเปลี่ยนสถานะ
    @Test
    void setTableStatus_whenIdExists_shouldUpdateStatus() {
        TableInfoEntity entity = new TableInfoEntity();
        entity.setStatus(TableStatusEnum.AVAILABLE);

        when(tableInfoRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(tableInfoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        tableInfoService.setTableStatus(1L, TableStatusEnum.OCCUPIED);

        assertThat(entity.getStatus()).isEqualTo(TableStatusEnum.OCCUPIED);
    }

    // ทดสอบ: setTableStatus โดยใช้ ID ที่ไม่เจอ → ควรโยน ResourceNotFoundException
    @Test
    void setTableStatus_whenIdNotFound_shouldThrowResourceNotFoundException() {
        when(tableInfoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tableInfoService.setTableStatus(1L, TableStatusEnum.OCCUPIED))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Table not found");
    }

    // ทดสอบ: ลบโต๊ะสำเร็จด้วย ID ที่มีอยู่
    @Test
    void deleteTable_whenIdExists_shouldDeleteSuccessfully() {
        when(tableInfoRepository.existsById(1L)).thenReturn(true);

        tableInfoService.deleteTable(1L);
        verify(tableInfoRepository).deleteById(1L);
    }

    // ทดสอบ: ลบโต๊ะด้วย ID ที่ไม่เจอ → ควรโยน ResourceNotFoundException
    @Test
    void deleteTable_whenIdNotExists_shouldThrowResourceNotFoundException() {
        when(tableInfoRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> tableInfoService.deleteTable(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Table with ID 1 not found");
    }

    // ทดสอบ: getEntityById สำเร็จ → ต้องได้ Entity
    @Test
    void getEntityById_whenExists_shouldReturnEntity() {
        TableInfoEntity entity = new TableInfoEntity();
        when(tableInfoRepository.findById(1L)).thenReturn(Optional.of(entity));

        TableInfoEntity result = tableInfoService.getEntityById(1L);
        assertThat(result).isEqualTo(entity);
    }

    // ทดสอบ: getEntityById ด้วย ID ที่ไม่มีอยู่ → ต้องโยน ResourceNotFoundException
    @Test
    void getEntityById_whenNotFound_shouldThrowResourceNotFoundException() {
        when(tableInfoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tableInfoService.getEntityById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Table not found");
    }
}
