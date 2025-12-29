package com.codewithdondamzy.onlinestore.Service;

import com.codewithdondamzy.onlinestore.Models.Customer;
import com.codewithdondamzy.onlinestore.Models.UserPrincipal;
import com.codewithdondamzy.onlinestore.Repository.CustomerRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final CustomerRepository customerRepository;

    public CustomUserDetailsService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Customer customer = customerRepository.findBuUserName(username);
        if (customer == null) {
            throw new UsernameNotFoundException("Customer with username " + username + " not found");
        }
        return new UserPrincipal(customer);
    }
}
