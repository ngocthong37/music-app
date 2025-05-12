package com.vanvan.musicapp.entity;

import com.vanvan.musicapp.Enum.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class User implements UserDetails {
    @Id
    @GeneratedValue
    private Integer id;

    @Column(unique = true, length = 50)
    private String username;

    @Column(length = 255)
    private String password;

    @Column(unique = true, length = 100)
    private String email;

    private String status;

    @Enumerated(EnumType.STRING)
    private Role role;

    private Date createdAt;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
