package com.example.api.models.entitys;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import static jakarta.persistence.GenerationType.SEQUENCE;

@Data
@Table
@NoArgsConstructor
@AllArgsConstructor
@Entity(name="categories")
public class CategoryEntity {

    @Id
    @SequenceGenerator(name = "categories_sequence", sequenceName = "categories_sequence", allocationSize = 1)
    @GeneratedValue(strategy = SEQUENCE, generator = "categories_sequence")
    private Long id;

    @Column(name = "name", nullable = false, unique = true, columnDefinition = "TEXT")
    private String name;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<PostEntity> posts;

    public CategoryEntity(String name){
        this.name = name;
    }
}
