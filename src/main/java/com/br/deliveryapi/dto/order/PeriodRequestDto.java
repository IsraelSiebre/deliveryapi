package com.br.deliveryapi.dto.order;

import java.time.LocalDateTime;

public record PeriodRequestDto (LocalDateTime start, LocalDateTime end) {}

