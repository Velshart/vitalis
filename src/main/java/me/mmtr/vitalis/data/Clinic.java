package me.mmtr.vitalis.data;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.mmtr.vitalis.data.enums.Specialization;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "clinics")
public class Clinic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;


    @Column(nullable = false)
    @Pattern(regexp = "[a-zA-ZąęćłńóśźżĄĘĆŁŃÓŚŹŻ ]+", message = "Nazwa ulicy składa się jedynie z liter.")
    private String street;

    @Column(nullable = false)
    @Pattern(regexp = "[0-9]+")
    private String buildingNumber;

    @Column(nullable = false)
    @Pattern(regexp = "\\d{2}-\\d{3}", message = "Prawidłowy format: XX-XXX.")
    private String postalCode;

    @Column(nullable = false)
    @Pattern(regexp = "[a-zA-ZąęćłńóśźżĄĘĆŁŃÓŚŹŻ ]+", message = "Nazwa miasta składa się jedynie z liter.")
    private String city;

    @Column(nullable = false)
    @Pattern(regexp = "\\d{9}", message = "Numer telefonu składa się z dokładnie 9 cyfr.")
    private String phoneNumber;


    @ManyToMany
    @JoinTable(
            name = "user_clinics",
            joinColumns = @JoinColumn(name = "clinic_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> employees = new HashSet<>();

    @ElementCollection(targetClass = Specialization.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "clinic_specializations", joinColumns = @JoinColumn(name = "clinic_id"))
    @Enumerated(EnumType.STRING)
    private Set<Specialization> specializations = new HashSet<>();

    public void addEmployee(User user) {
        this.employees.add(user);
        user.getClinics().add(this);
    }

    public void removeEmployee(User user) {
        this.employees.remove(user);
        user.getClinics().remove(this);
    }
}
