package com.codewithdondamzy.onlinestore.Models;

import com.codewithdondamzy.onlinestore.Dtos.Request.CreateCustomerRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
    private List<Address> addresses = new ArrayList<>();

    @OneToOne(mappedBy = "customer",cascade = CascadeType.ALL, orphanRemoval = true)
    private Cart cart;
    private String email;
    private String password;
    private String userName;
    private String UUID;

    @OneToMany(mappedBy = "customer",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders;

    @OneToOne
    private Order order;
    @OneToMany(mappedBy = "customer",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> review;
    private boolean isActive;
    private LocalDate createdAt;
    private Role role;

    public String getPassword() {
        if (CreateCustomerRequest.isValidPassword(password))
            return BCrypt.hashpw(password, BCrypt.gensalt());
        else
            throw new RuntimeException("password must contain at least one " +
                    "capital letter, small letter and special characters");
    }


    private static boolean isValidPassword(String password) {
        if(password == null) {
            return false;
        }
        return password.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*[@#$%^&+=!]).{8,}$");
    }
}
