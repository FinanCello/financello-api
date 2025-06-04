package com.example.financelloapi.service;

import lombok.RequiredArgsConstructor;

import com.example.financelloapi.dto.request.SpendingLimitRequest;
import com.example.financelloapi.dto.test.SpendingLimitResponse;
import com.example.financelloapi.dto.test.SpendingLimitAlertResponse;
import com.example.financelloapi.exception.DuplicateLimitException;
import com.example.financelloapi.exception.InvalidLimitAmountException;
import com.example.financelloapi.exception.UserDoesntExistException;
import com.example.financelloapi.mapper.SpendingLimitMapper;
import com.example.financelloapi.model.entity.Category;
import com.example.financelloapi.model.entity.SpendingLimit;
import com.example.financelloapi.model.entity.User;
import com.example.financelloapi.model.entity.FinancialMovement;
import com.example.financelloapi.model.enums.MovementType;
import com.example.financelloapi.repository.SpendingLimitRepository;
import com.example.financelloapi.repository.CategoryRepository;
import com.example.financelloapi.repository.UserRepository;
import com.example.financelloapi.repository.FinancialMovementRepository;
import com.example.financelloapi.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.util.List;
import java.time.LocalDate;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class SpendingLimitService {
    private final SpendingLimitRepository spendingLimitRepository;
    private final CategoryRepository categoryRepository;
    private final AuthService authService;
    private final SpendingLimitMapper spendingLimitMapper;
    private final UserRepository userRepository;
    private final FinancialMovementRepository financialMovementRepository;

    @Transactional
    public SpendingLimitResponse createSpendingLimit(Integer userId, SpendingLimitRequest request) {
        if (request.monthlyLimit() <=0){
            throw new InvalidLimitAmountException("El limite debe de ser mayor que 0");
        }

        boolean exists = spendingLimitRepository
                .findByCategory_IdAndUser_Id(request.categoryId(), userId)
                .isPresent();

        if (exists) {
            throw new DuplicateLimitException("Ya existe un limite para esta categoria");
        }

        User user = userRepository.findByIdCustom(userId)
                .orElseThrow(() -> new UserDoesntExistException("Usuario no encontrado"));

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(()-> new IllegalArgumentException("Categoria no encontrada"));

        SpendingLimit newLimit = new SpendingLimit();
        newLimit.setCategory(category);
        newLimit.setMonthlyLimit(request.monthlyLimit());
        newLimit.setUser(user);

        SpendingLimit savedLimit = spendingLimitRepository.save(newLimit);

        return spendingLimitMapper.toResponse(savedLimit);

    }

    @Transactional
    public List<SpendingLimitResponse> listSpendingLimits(Integer userId) {
        return spendingLimitRepository
                .findByUser_Id(userId)
                .stream()
                .map(spendingLimitMapper::toResponse)
                .toList();
    }

    @Transactional
    public void deleteByCategory(Integer userId, Integer categoryId) {
        SpendingLimit limit = spendingLimitRepository
                .findByCategory_IdAndUser_Id(categoryId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No existe un límite para la categoría " + categoryId));
        spendingLimitRepository.delete(limit);
    }


    @Transactional
    public List<SpendingLimitAlertResponse> getAlerts(Integer userId) {
        List<SpendingLimit> limits = spendingLimitRepository.findByUser_Id(userId);
        List<SpendingLimitAlertResponse> alerts = new ArrayList<>();

        LocalDate start = LocalDate.now().withDayOfMonth(1);
        LocalDate end = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());

        for (SpendingLimit limit : limits) {
            List<FinancialMovement> expenses = financialMovementRepository
                    .findByUser_IdAndCategory_IdAndMovementTypeAndDateBetween(
                            userId,
                            limit.getCategory().getId(),
                            MovementType.OUTCOME,
                            start,
                            end
                    );

            float totalSpent = expenses.stream()
                    .map(FinancialMovement::getAmount)
                    .reduce(0f, Float::sum);

            boolean overLimit = totalSpent > limit.getMonthlyLimit();

            String alertMessage = overLimit
                    ? "¡Has superado tu limite en la categoria" + limit.getCategory().getName() + "!"
                    : "";

            alerts.add(new SpendingLimitAlertResponse(
                    limit.getCategory().getName(),
                    limit.getMonthlyLimit(),
                    totalSpent,
                    overLimit,
                    alertMessage
            ));
        }
        return alerts;
    }

}
