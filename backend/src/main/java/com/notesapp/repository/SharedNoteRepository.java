package com.notesapp.repository;

import com.notesapp.model.SharedNote;
import com.notesapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SharedNoteRepository extends JpaRepository<SharedNote, Long> {
    List<SharedNote> findByTargetUser(User targetUser);
}
