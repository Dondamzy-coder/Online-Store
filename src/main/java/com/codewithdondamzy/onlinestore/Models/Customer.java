package com.codewithdondamzy.onlinestore.Models;

import com.codewithdondamzy.onlinestore.Dtos.Request.CreateCustomerRequest;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@ToString(exclude = "addresses")
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;
    private String name;


    @OneToMany(mappedBy = "customer",cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Address> addresses = new ArrayList<>();

    @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Cart cart;

    private String email;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private String userName;
    private String UUID;
    private String phoneNumber;
    @OneToMany(mappedBy = "customer",cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Order> orders;

    @OneToMany(mappedBy = "customer",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> review;
    private boolean isActive;
    private LocalDate createdAt;

    @Enumerated(EnumType.STRING)
    private Role role;

//    public String getPassword() {
//        if (CreateCustomerRequest.isValidPassword(password))
//            return BCrypt.hashpw(password, BCrypt.gensalt());
//        else
//            throw new RuntimeException("password must contain at least one " +
//                    "capital letter, small letter and special characters");
//    }


//    private static boolean isValidPassword(String password) {
//        if(password == null) {
//            return false;
//        }
//        return password.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*[@#$%^&+=!]).{8,}$");
//    }
}
