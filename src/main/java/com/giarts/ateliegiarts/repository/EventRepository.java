package com.giarts.ateliegiarts.repository;

import com.giarts.ateliegiarts.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    @Query(value = """
            SELECT *
            FROM events
            """, countQuery = """
            SELECT COUNT(*)
            FROM events
            """, nativeQuery = true)
    Page<Event> findAllEventsPaginated(Pageable pageable);
}
