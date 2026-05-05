package com.notesapp.repository;

import com.notesapp.model.GroupEntity;
import com.notesapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GroupRepository extends JpaRepository<GroupEntity, Long> {
    List<GroupEntity> findByOwner(User owner);
}
