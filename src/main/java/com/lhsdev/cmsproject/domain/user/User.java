package com.lhsdev.cmsproject.domain.user;

import com.lhsdev.cmsproject.domain.membership.MembershipGrade;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column
    private String password;

    @Column
    private String picture;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthProvider provider;

    private String providerId;

    private int totalPoints = 0;

    private int availablePoints = 0;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "grade_id")
    private MembershipGrade grade;

    @Builder
    public User(String name, String email, String password, String picture, Role role, AuthProvider provider,
            String providerId) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.picture = picture;
        this.role = role;
        this.provider = provider;
        this.providerId = providerId;
    }

    public User update(String name, String picture) {
        this.name = name;
        this.picture = picture;
        return this;
    }

    public String getRoleKey() {
        return this.role.getKey();
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void updateRole(Role role) {
        this.role = role;
    }
}
