package me.mmtr.vitalis.data.enums;

import lombok.Getter;

@Getter
public enum InvitationStatus {
    PENDING("Oczek. na zatwierdzenie");

    private final String status;

    InvitationStatus(String status) {
        this.status = status;
    }
}
