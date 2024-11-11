package me.mmtr.vitalis.data.enums;

import lombok.Getter;

@Getter
public enum AppointmentStatus {
    PENDING("Oczek. na zatwierdzenie"),
    SCHEDULED("Zatwierdzona"),
    REJECTED("Odrzucona"),
    CANCELLED("Anulowana"),
    COMPLETED("Zakończona");

    private final String status;

    AppointmentStatus(String status) {
        this.status = status;
    }
}
