package secretstuffs.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import secretstuffs.domain.enums.CategoryEnum;
import secretstuffs.domain.enums.ConditionEnum;
import secretstuffs.domain.enums.ItemPostStatusEnum;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "item_posts")
public class ItemPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true)
    private Long id;

    @NotBlank(message = "Email cannot be blank.")
    @Email(message = "Invalid email format.")
    private String email;

    @NotBlank(message = "Title cannot be blank.")
    private String title;  // Title of the item post

    @NotBlank(message = "Address cannot be blank.")
    private String address;  // User's address

    @NotBlank(message = "Description cannot be blank.")
    private String description;  // Description of the item post

    @Enumerated(EnumType.STRING)
    private CategoryEnum category;  // Category of the item (e.g., FURNITURE, ELECTRONICS)

    private String itemPostImageUrl;  // URL for the item's profile image

    @Enumerated(EnumType.STRING)
    private ConditionEnum condition;  // Condition of the item (e.g., NEW, GOOD, POOR)

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ItemPostStatusEnum status = ItemPostStatusEnum.ACTIVE; // Status of the item post (e.g., ACTIVE, INACTIVE, DONATED)
}
