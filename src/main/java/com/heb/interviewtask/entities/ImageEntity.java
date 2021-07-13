package com.heb.interviewtask.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.server.core.Relation;

import javax.persistence.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "images")
@Relation(collectionRelation = "images")
public class ImageEntity {
    @Id
    @Column(name="image_checksum")
    private String hash;

    @Column(name="filename")
    private String fileName;

    @OneToMany(mappedBy="image", cascade=CascadeType.ALL)
    private List<TagEntity> tags;
}
