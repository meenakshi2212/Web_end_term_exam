package com.notesapp.repository;

import com.notesapp.model.Note;
import com.notesapp.model.VersionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VersionHistoryRepository extends JpaRepository<VersionHistory, Long> {
    List<VersionHistory> findByNoteOrderByVersionAtDesc(Note note);
}
