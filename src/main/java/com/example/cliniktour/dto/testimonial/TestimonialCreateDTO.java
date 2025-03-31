package com.example.cliniktour.dto.testimonial;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestimonialCreateDTO {

    @NotBlank(message = "Имя обязательно")
    @Size(max = 100, message = "Имя не должно превышать 100 символов")
    private String clientName;

    @Size(max = 100, message = "Страна не должна превышать 100 символов")
    private String clientCountry;

    @Size(max = 100, message = "Город не должен превышать 100 символов")
    private String clientCity;

    @Size(max = 150, message = "Тип лечения не должен превышать 150 символов")
    private String treatmentType;

    @NotNull(message = "ID клиники обязателен")
    private Long clinicId;

    @NotNull(message = "Рейтинг обязателен")
    @Min(value = 1, message = "Рейтинг должен быть не менее 1")
    @Max(value = 5, message = "Рейтинг должен быть не более 5")
    private Integer rating;

    @NotBlank(message = "Текст отзыва обязателен")
    private String reviewText;

    @Email(message = "Введите корректный email адрес")
    @Size(max = 150, message = "Email не должен превышать 150 символов")
    @NotNull(message = "Email адрес обязателен ")
    private String clientEmail;

    private String clientPhotoUrl;

    @NotNull(message = "Поле 'Рекомендую' обязательно")
    private Boolean wouldRecommend;
}