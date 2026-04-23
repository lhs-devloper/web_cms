package com.lhsdev.cmsproject.service;

import com.lhsdev.cmsproject.domain.membership.MembershipGrade;
import com.lhsdev.cmsproject.domain.user.User;
import com.lhsdev.cmsproject.repository.MembershipGradeRepository;
import com.lhsdev.cmsproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MembershipService {

    private final UserRepository userRepository;
    private final MembershipGradeRepository gradeRepository;

    @Transactional
    public void addPoints(Long userId, int points) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setTotalPoints(user.getTotalPoints() + points);
        user.setAvailablePoints(user.getAvailablePoints() + points);
        recalculateGrade(user);
    }

    @Transactional
    public void usePoints(Long userId, int points) {
        User user = userRepository.findById(userId).orElseThrow();
        if (user.getAvailablePoints() < points) {
            throw new IllegalStateException("포인트가 부족합니다.");
        }
        user.setAvailablePoints(user.getAvailablePoints() - points);
    }

    @Transactional
    public void adjustPoints(Long userId, int totalPoints, int availablePoints) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setTotalPoints(totalPoints);
        user.setAvailablePoints(availablePoints);
        recalculateGrade(user);
    }

    private void recalculateGrade(User user) {
        List<MembershipGrade> grades = gradeRepository.findByIsActiveTrueOrderBySortOrderAsc();
        MembershipGrade bestGrade = null;
        for (MembershipGrade g : grades) {
            if (user.getTotalPoints() >= g.getMinPoints()) {
                bestGrade = g;
            }
        }
        if (bestGrade != null) {
            user.setGrade(bestGrade);
        }
    }

    @Transactional(readOnly = true)
    public List<MembershipGrade> getActiveGrades() {
        return gradeRepository.findByIsActiveTrueOrderBySortOrderAsc();
    }

    @Transactional(readOnly = true)
    public List<MembershipGrade> getAllGrades() {
        return gradeRepository.findAllByOrderBySortOrderAsc();
    }
}
