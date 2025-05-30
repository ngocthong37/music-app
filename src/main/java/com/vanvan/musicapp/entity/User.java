package com.vanvan.musicapp.entity;

import com.vanvan.musicapp.Enum.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Column(name = "reset_token", length = 255)
    private String resetToken;

    @Column(name = "reset_token_expiry")
    private LocalDateTime resetTokenExpiry;

    @Column(name = "verification_token", length = 255)
    private String verificationToken; // Thêm trường này

    @Column(name = "is_verified", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isVerified;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    public String getName() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !"NOT_ACTIVE".equals(status);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isVerified && "ACTIVE".equals(status);
    }
}
