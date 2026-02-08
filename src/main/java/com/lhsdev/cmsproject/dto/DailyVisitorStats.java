package com.lhsdev.cmsproject.dto;

import java.time.LocalDate;

public interface DailyVisitorStats {
    LocalDate getDate();

    Long getCount();
}
