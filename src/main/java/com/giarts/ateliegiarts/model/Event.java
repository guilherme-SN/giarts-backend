package com.giarts.ateliegiarts.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.giarts.ateliegiarts.dto.EventDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private LocalDateTime dateTime;

    @JsonIgnore
    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EventImage> eventImages;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public Event(EventDTO eventDTO) {
        this.name = eventDTO.getName();
        this.description = eventDTO.getDescription();
        this.location = eventDTO.getLocation();
        this.dateTime = eventDTO.getDateTime();
    }
}
