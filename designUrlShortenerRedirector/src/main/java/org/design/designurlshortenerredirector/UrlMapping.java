package org.design.designurlshortenerredirector;

//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;
//import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

//@Entity
@Table(name = "url_mapping")
@Getter
@Setter
@NoArgsConstructor
public class UrlMapping {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @Column(name = "short_code", unique = true, nullable = false)
    @Column("short_code")
    private String shortCode;

//    @Column(name = "target", nullable = false)
    @Column("target")
    private String originalUrl;

//    @Column(name = "created_at")
    @Column("created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}