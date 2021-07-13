package com.heb.interviewtask.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name="tags")
public class TagEntity {
    public TagEntity(String name, double confidence, String checksum){
        this.name = name;
        this.confidence = confidence;
        this.image = checksum;
    }

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id")
    private long id;

    @Column(name="name")
    private String name;

    @Column(name="confidence")
    private double confidence;

    @Column(name="image")
    private String image;
}
