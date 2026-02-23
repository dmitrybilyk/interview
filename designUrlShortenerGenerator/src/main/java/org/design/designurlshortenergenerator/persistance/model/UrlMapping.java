package org.design.designurlshortenergenerator.persistance.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "url_mapping",
       uniqueConstraints = {@UniqueConstraint(columnNames = {"short_code"})})
public class UrlMapping {
    @Id
    private Long id;

    @Column(name = "short_code", nullable = false, length = 12)
    private String shortCode;

    @Column(name = "target", nullable = false, columnDefinition = "text")
    private String target;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();



}
