package com.jamlsolution.backend.product.wishlist;

import com.jamlsolution.backend.common.BaseEntity;
import com.jamlsolution.backend.identity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "wishlists")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Wishlist extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name = "My Wishlist";

    @Column(name = "is_default")
    private boolean isDefault = false;

    @Column(name = "is_public")
    private boolean isPublic = false;

    @Column(name = "share_token", unique = true)
    private String shareToken;

    @OneToMany(mappedBy = "wishlist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WishlistItem> items = new ArrayList<>();
}
