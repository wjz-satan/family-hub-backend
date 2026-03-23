package com.familyhub.backend.repository;

import com.familyhub.backend.entity.Bill;
import com.familyhub.backend.enums.BillType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BillRepository extends JpaRepository<Bill, Long> {
    List<Bill> findByFamilyIdAndBillDateBetweenOrderByBillDateDesc(Long familyId, LocalDate start, LocalDate end);

    @Query("select coalesce(sum(b.amount), 0) from Bill b where b.familyId = :familyId and b.type = :type and b.billDate between :start and :end")
    Optional<BigDecimal> sumAmount(@Param("familyId") Long familyId,
                                   @Param("type") BillType type,
                                   @Param("start") LocalDate start,
                                   @Param("end") LocalDate end);
}
