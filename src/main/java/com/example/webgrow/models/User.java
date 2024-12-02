package com.example.webgrow.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name= "_user")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String firstName;
    private String lastName;
    private String email;
    private String mobile;
    @JsonIgnore
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;
    @JsonIgnore
    private String otp;
    private LocalDateTime generatedAt;
    private String designation;
    private String organization;
    private boolean verified;
    private boolean enabled;
    private String imageUrl;
    private Long coins = 0L;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
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
        return verified;
    }

    @OneToMany(mappedBy = "host", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Quiz> hostedQuizzes;

    @ManyToMany(mappedBy = "participants")
    @JsonIgnore
    private List<Quiz> participatedQuizzes;

    @OneToMany(mappedBy = "host", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Event> hostedEvents;

    @ManyToMany(mappedBy = "participants")
    @JsonIgnore
    private List<Event> participatedEvents;

    @ManyToMany(mappedBy = "administrators")
    @JsonBackReference
    private List<Event> managedEvents = new ArrayList<>();


}
