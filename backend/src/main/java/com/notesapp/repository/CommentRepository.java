package com.notesapp.repository;

import com.notesapp.model.Comment;
import com.notesapp.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByNoteOrderByCreatedAtDesc(Note note);
}
