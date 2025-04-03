package com.example.cliniktour.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BlogPostType {
    INTERESTING("Интересные"),
    CLINICS("Клиники"),
    PATIENT_ADVICE("Советы пациентам"),
    TREATMENT_ABROAD("Лечение за рубежом"),
    NEWS("Новости медицины"),
    TECHNOLOGY("Медицинские технологии"),
    DOCTORS("Врачи и специалисты"),
    SUCCESS_STORIES("Истории успеха"),
    RESEARCH("Исследования и открытия"),
    WELLNESS("Здоровый образ жизни"),
    NUTRITION("Питание и диеты"),
    PROCEDURES("Процедуры и операции"),
    INTERVIEWS("Интервью с экспертами"),
    FAQ("Часто задаваемые вопросы"),
    COSTS("Стоимость лечения");

    private final String displayName;
}