package com.giarts.ateliegiarts.repository;

import com.giarts.ateliegiarts.model.EventImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventImageRepository extends JpaRepository<EventImage, Long> {
    List<EventImage> findAllByEventId(Long eventId);
}
