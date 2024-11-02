package me.mmtr.vitalis.data;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String street;

    @Column(nullable = false)
    private String buildingNumber;

    @Column(nullable = false)
    private String postalCode;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String phoneNumber;


    @ManyToMany
    @JoinTable(
            name = "user_clinics",
            joinColumns = @JoinColumn(name = "clinic_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> employees = new HashSet<>();

    public void addEmployee(User user) {
        this.employees.add(user);
        user.getClinics().add(this);
    }

    public void removeEmployee(User user) {
        this.employees.remove(user);
        user.getClinics().remove(this);
    }
}
