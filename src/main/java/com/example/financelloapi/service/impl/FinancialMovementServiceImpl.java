package com.example.financelloapi.service.impl;

import com.example.financelloapi.dto.request.RegisterFinancialMovementRequest;
import com.example.financelloapi.dto.test.RegisterFinancialMovementResponse;
import com.example.financelloapi.dto.test.TransactionResponse;
import com.example.financelloapi.exception.CategoryNotFoundException;
import com.example.financelloapi.exception.CustomException;
import com.example.financelloapi.exception.UserDoesntExistException;
import com.example.financelloapi.mapper.FinancialMovementMapper;
import com.example.financelloapi.mapper.TransactionMapper;
import com.example.financelloapi.model.entity.Category;
import com.example.financelloapi.model.entity.FinancialMovement;
import com.example.financelloapi.model.enums.CurrencyType;
import com.example.financelloapi.model.enums.MovementType;
import com.example.financelloapi.repository.CategoryRepository;
import com.example.financelloapi.repository.FinancialMovementRepository;
import com.example.financelloapi.repository.UserRepository;
import com.example.financelloapi.service.FinancialMovementService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@RequiredArgsConstructor
@Service
public class FinancialMovementServiceImpl implements FinancialMovementService {
    private final CategoryRepository categoryRepository;
    private final FinancialMovementRepository financialMovementRepository;
    private final FinancialMovementMapper financialMovementMapper;
    private final UserRepository userRepository;

    @Override
    public RegisterFinancialMovementResponse registerMovement(Integer userId, RegisterFinancialMovementRequest request) {
        if (request.amount() == null || request.amount().doubleValue() <= 0) {
            throw new CustomException("Amount must be greater than or equal to 0.");
        }
        if (request.date().isBefore(LocalDate.now())) {
            throw new CustomException("La fecha no puede estar en el pasado.");
        }

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new CategoryNotFoundException("Category not found."));

        List<FinancialMovement> existingMovements = financialMovementRepository
                .findByUser_IdAndMovementType(userId, request.movementType());

        boolean isDuplicate = existingMovements.stream().anyMatch(m ->
                Float.compare(m.getAmount(), request.amount()) == 0 &&
                        m.getDate().equals(request.date()) &&
                        m.getCategory().getId().equals(request.categoryId())
        );

        if (isDuplicate) {
            throw new CustomException("Movimiento duplicado");
        }

        FinancialMovement movement = financialMovementMapper.toEntity(request, category);
        movement.setUser(userRepository.getById(userId));
        financialMovementRepository.save(movement);

        return financialMovementMapper.toRegisterFinancialMovementResponse(movement);
    }

    @Override
    public List<TransactionResponse> getMovementsByUserIdFiltered(Integer id, String movementTypeName, Integer categoryId) {
        if (!userRepository.existsById(id)) {
            throw new UserDoesntExistException("User not found with ID: " + id);
        }

        try {
            List<FinancialMovement> movements;

            if (movementTypeName != null) {
                movements = financialMovementRepository.findByUser_IdAndMovementTypeOrderByDateDesc(
                        id, MovementType.valueOf(movementTypeName.toUpperCase()));
            } else {
                movements = financialMovementRepository.findByUser_IdOrderByDateDesc(id);
            }

            return movements.stream()
                    .map(TransactionMapper::toResponse)
                    .toList();

        } catch (DataAccessException e) {
            throw new CustomException("Error al cargar historial");
        }
    }

    @Override
    public List<RegisterFinancialMovementResponse> filterMovements(Integer userId, Integer categoryId, MovementType type) {
        List<FinancialMovement> movimientos;

        if (categoryId != null && type != null) {
            movimientos = financialMovementRepository.findByUser_IdAndCategory_IdAndMovementType(userId, categoryId, type);
        } else if (categoryId != null) {
            movimientos = financialMovementRepository.findByUser_IdAndCategory_Id(userId, categoryId);
        } else if (type != null) {
            movimientos = financialMovementRepository.findByUser_IdAndMovementType(userId, type);
        } else {
            movimientos = financialMovementRepository.findByUser_Id(userId);
        }

        return movimientos.stream()
                .map(financialMovementMapper::toRegisterFinancialMovementResponse)
                .toList();
    }

    @Transactional
    public void importFromExcel(MultipartFile file) {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;

                Float amount = (float) row.getCell(0).getNumericCellValue();
                String currencyTypeStr = getCellStringValue(row.getCell(1));
                String movementTypeStr = getCellStringValue(row.getCell(3));
                int categoryId = (int) row.getCell(4).getNumericCellValue();
                int userId = (int) row.getCell(5).getNumericCellValue();

                Cell dateCell = row.getCell(2);
                LocalDate date;
                if (dateCell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(dateCell)) {
                    date = dateCell.getLocalDateTimeCellValue().toLocalDate();
                } else {
                    date = LocalDate.parse(dateCell.getStringCellValue());
                }

                MovementType movementType = MovementType.valueOf(movementTypeStr.toUpperCase());
                CurrencyType currencyType = CurrencyType.valueOf(currencyTypeStr.toUpperCase());

                RegisterFinancialMovementRequest request = new RegisterFinancialMovementRequest(
                        amount, date, movementType, categoryId, currencyType
                );

                registerMovement(userId, request);
            }

        } catch (IOException | IllegalArgumentException e) {
            throw new RuntimeException("Error al importar Excel: " + e.getMessage(), e);
        }
    }

    private String getCellStringValue(Cell cell) {
        if (cell == null) return "";

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toLocalDate().toString();
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

}
