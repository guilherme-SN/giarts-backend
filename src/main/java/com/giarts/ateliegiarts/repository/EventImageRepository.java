package com.giarts.ateliegiarts.repository;

import com.giarts.ateliegiarts.model.EventImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventImageRepository extends JpaRepository<EventImage, Long> {
    @Query(value = """
            SELECT *
            FROM event_images
            WHERE event_id = :eventId
            """, nativeQuery = true)
    List<EventImage> findAllByEventId(@Param(value = "eventId") Long eventId);
}
