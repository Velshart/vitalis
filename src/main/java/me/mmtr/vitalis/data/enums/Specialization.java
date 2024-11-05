package me.mmtr.vitalis.data.enums;

import lombok.Getter;

@Getter
public enum Specialization {
    CARDIOLOGY("Kardiologia"),
    PEDIATRICS("Pediatria"),
    DERMATOLOGY("Dermatologia"),
    ORTHOPEDICS("Ortopedia"),
    GYNECOLOGY("Ginekologia"),
    ONCOLOGY("Onkologia"),
    NEUROLOGY("Neurologia"),
    PSYCHIATRY("Psychiatria"),
    OPHTHALMOLOGY("Okulistyka"),
    UROLOGY("Urologia"),
    ENDOCRINOLOGY("Endokrynologia"),
    DIABETOLOGY("Diabetologia"),
    HEMATOLOGY("Hematologia"),
    RHEUMATOLOGY("Reumatologia"),
    GASTROENTEROLOGY("Gastroenterologia"),
    OTOLARYNGOLOGY("Laryngologia"),
    NEPHROLOGY("Nefrologia"),
    GENERAL_SURGERY("Chirurgia ogólna"),
    VASCULAR_SURGERY("Chirurgia naczyniowa"),
    ONCOLOGICAL_SURGERY("Chirurgia onkologiczna"),
    PLASTIC_SURGERY("Chirurgia plastyczna"),
    MAXILLOFACIAL_SURGERY("Chirurgia szczękowo-twarzowa"),
    RADIOLOGY("Radiologia"),
    FAMILY_MEDICINE("Medycyna rodzinna"),
    SPORTS_MEDICINE("Medycyna sportowa"),
    AESTHETIC_MEDICINE("Medycyna estetyczna"),
    OCCUPATIONAL_MEDICINE("Medycyna pracy"),
    ANESTHESIOLOGY("Anestezjologia"),
    PHYSIOTHERAPY("Fizjoterapia"),
    PSYCHOTHERAPY("Psychoterapia"),
    SPEECH_THERAPY("Logopedia"),
    PULMONOLOGY("Pulmonologia"),
    IMMUNOLOGY("Immunologia"),
    GERIATRICS("Geriatria"),
    ALLERGOLOGY("Alergologia"),
    INFECTOLOGY("Infectologia"),
    TOXICOLOGY("Toksykologia");

    private final String name;

    Specialization(String name) {
        this.name = name;
    }
}
