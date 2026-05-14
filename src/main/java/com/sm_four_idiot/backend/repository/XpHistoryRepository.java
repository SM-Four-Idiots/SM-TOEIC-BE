package com.sm_four_idiot.backend.repository;

import com.sm_four_idiot.backend.domain.User;
import com.sm_four_idiot.backend.domain.XpHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface XpHistoryRepository extends JpaRepository<XpHistory, Long> {
    List<XpHistory> findTop10ByUserOrderByEarnedAtDesc(User user);
}