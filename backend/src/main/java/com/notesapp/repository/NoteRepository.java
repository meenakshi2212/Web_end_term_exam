package com.notesapp.repository;

import com.notesapp.model.Note;
import com.notesapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByOwner(User owner);

    @Query("SELECT DISTINCT n FROM Note n LEFT JOIN n.tags t WHERE LOWER(n.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(n.content) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(t.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Note> search(@Param("query") String query);
}
