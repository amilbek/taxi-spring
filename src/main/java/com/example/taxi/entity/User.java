package com.example.taxi.entity;

import com.example.taxi.enums.Status;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="users")
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String driverIdNumber;
    private String driverLicenseNumber;
    private Date licenseExpDate;
    private Boolean isDriver;
    private Boolean isAvailable;
    private String username;
    private String password;
    private Double rating;
    @ManyToOne(fetch = FetchType.EAGER)
    private Role role;
    @Enumerated(value = EnumType.STRING)
    private Status status;

    public User(final String firstName, final String lastName, final String phoneNumber,
                final String username, final String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.isDriver = false;
        this.isAvailable = false;
        this.rating = 0.0;
        this.username = username;
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", driverIdNumber='" + driverIdNumber + '\'' +
                ", driverLicenseNumber='" + driverLicenseNumber + '\'' +
                ", licenseExpDate=" + licenseExpDate +
                ", isDriver=" + isDriver +
                ", isAvailable=" + isAvailable +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", rating" + rating +
                ", role=" + role +
                ", status=" + status +
                '}';
    }
}
