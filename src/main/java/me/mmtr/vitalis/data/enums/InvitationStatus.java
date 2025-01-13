package me.mmtr.vitalis.data.enums;

import lombok.Getter;

@Getter
public enum InvitationStatus {
    PENDING("Oczek. na zatwierdzenie"),
    ACCEPT("Zatwierdź"),
    REJECT("Odrzuć");

    private final String status;

    InvitationStatus(String status) {
        this.status = status;
    }
}
