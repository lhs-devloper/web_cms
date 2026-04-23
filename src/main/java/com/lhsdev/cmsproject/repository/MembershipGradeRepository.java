package com.lhsdev.cmsproject.repository;

import com.lhsdev.cmsproject.domain.membership.MembershipGrade;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface MembershipGradeRepository extends JpaRepository<MembershipGrade, Long> {
    List<MembershipGrade> findByIsActiveTrueOrderBySortOrderAsc();
    List<MembershipGrade> findAllByOrderBySortOrderAsc();
    Optional<MembershipGrade> findByCode(String code);
}
