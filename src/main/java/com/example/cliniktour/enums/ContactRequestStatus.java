package com.example.cliniktour.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ContactRequestStatus {
    NEW("Новая заявка"),
    IN_PROGRESS("В обработке"),
    RESPONDED("Ответ отправлен"),
    CLOSED("Закрыта");

    private final String displayName;

}