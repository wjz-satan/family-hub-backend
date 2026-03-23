package com.familyhub.backend.service;

import com.familyhub.backend.common.AppException;
import com.familyhub.backend.dto.BillDtos;
import com.familyhub.backend.entity.Bill;
import com.familyhub.backend.enums.BillType;
import com.familyhub.backend.repository.BillRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BillService {

    private final BillRepository billRepository;
    private final AccessService accessService;

    public BillService(BillRepository billRepository, AccessService accessService) {
        this.billRepository = billRepository;
        this.accessService = accessService;
    }

    public List<BillDtos.BillResponse> list(Long familyId, Long userId, LocalDate start, LocalDate end) {
        accessService.requireMembership(familyId, userId);
        LocalDate actualStart = start == null ? YearMonth.now().atDay(1) : start;
        LocalDate actualEnd = end == null ? YearMonth.now().atEndOfMonth() : end;
        return billRepository.findByFamilyIdAndBillDateBetweenOrderByBillDateDesc(familyId, actualStart, actualEnd).stream()
                .map(this::toResponse)
                .toList();
    }

    public BillDtos.BillResponse create(Long familyId, Long userId, BillDtos.UpsertBillRequest request) {
        accessService.requireMembership(familyId, userId);
        accessService.ensureMemberBelongsToFamily(familyId, request.memberId());
        Bill bill = new Bill();
        writeBill(bill, familyId, request);
        return toResponse(billRepository.save(bill));
    }

    public BillDtos.BillResponse update(Long familyId, Long billId, Long userId, BillDtos.UpsertBillRequest request) {
        accessService.requireMembership(familyId, userId);
        accessService.ensureMemberBelongsToFamily(familyId, request.memberId());
        Bill bill = requireBill(familyId, billId);
        writeBill(bill, familyId, request);
        return toResponse(billRepository.save(bill));
    }

    public void delete(Long familyId, Long billId, Long userId) {
        accessService.requireAdmin(familyId, userId);
        billRepository.delete(requireBill(familyId, billId));
    }

    public BillDtos.BillStatisticsResponse statistics(Long familyId, Long userId, YearMonth month) {
        accessService.requireMembership(familyId, userId);
        YearMonth actualMonth = month == null ? YearMonth.now() : month;
        LocalDate start = actualMonth.atDay(1);
        LocalDate end = actualMonth.atEndOfMonth();
        BigDecimal income = billRepository.sumAmount(familyId, BillType.INCOME, start, end).orElse(BigDecimal.ZERO);
        BigDecimal expense = billRepository.sumAmount(familyId, BillType.EXPENSE, start, end).orElse(BigDecimal.ZERO);
        Map<String, BigDecimal> expenseByCategory = billRepository.findByFamilyIdAndBillDateBetweenOrderByBillDateDesc(familyId, start, end).stream()
                .filter(bill -> bill.getType() == BillType.EXPENSE)
                .collect(Collectors.groupingBy(Bill::getCategory, LinkedHashMap::new, Collectors.reducing(BigDecimal.ZERO, Bill::getAmount, BigDecimal::add)));
        return new BillDtos.BillStatisticsResponse(income, expense, income.subtract(expense), expenseByCategory);
    }

    private Bill requireBill(Long familyId, Long billId) {
        return billRepository.findById(billId)
                .filter(bill -> bill.getFamilyId().equals(familyId))
                .orElseThrow(() -> new AppException(404, "账单不存在"));
    }

    private void writeBill(Bill bill, Long familyId, BillDtos.UpsertBillRequest request) {
        bill.setFamilyId(familyId);
        bill.setMemberId(request.memberId());
        bill.setType(request.type());
        bill.setAmount(request.amount());
        bill.setCategory(request.category());
        bill.setPaymentMethod(request.paymentMethod());
        bill.setBillDate(request.billDate());
        bill.setNote(request.note());
    }

    private BillDtos.BillResponse toResponse(Bill bill) {
        return new BillDtos.BillResponse(
                bill.getId(),
                bill.getFamilyId(),
                bill.getMemberId(),
                bill.getType(),
                bill.getAmount(),
                bill.getCategory(),
                bill.getPaymentMethod(),
                bill.getBillDate(),
                bill.getNote()
        );
    }
}
