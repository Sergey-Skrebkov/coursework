package ru.template.example.documents.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.Instant;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "document")
public class DocumentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "type", nullable = false)
    @Type(type = "org.hibernate.type.TextType")
    private String type;

    @Column(name = "organization", nullable = false)
    @Type(type = "org.hibernate.type.TextType")
    private String organization;

    @Column(name = "date")
    private Instant date;

    @Column(name = "patient")
    @Type(type = "org.hibernate.type.TextType")
    private String patient;

    @Column(name = "state")
    @Type(type = "org.hibernate.type.TextType")
    private String state;

    @Column(name = "description")
    @Type(type = "org.hibernate.type.TextType")
    private String description;

}
