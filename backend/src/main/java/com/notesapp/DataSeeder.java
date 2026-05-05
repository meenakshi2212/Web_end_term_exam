package com.notesapp;

import com.notesapp.controller.AuthController;
import com.notesapp.model.Note;
import com.notesapp.model.Tag;
import com.notesapp.model.User;
import com.notesapp.repository.NoteRepository;
import com.notesapp.repository.TagRepository;
import com.notesapp.repository.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Set;

@Component
public class DataSeeder implements ApplicationRunner {

    private final UserRepository userRepository;
    private final NoteRepository noteRepository;
    private final TagRepository tagRepository;

    public DataSeeder(UserRepository userRepository, NoteRepository noteRepository, TagRepository tagRepository) {
        this.userRepository = userRepository;
        this.noteRepository = noteRepository;
        this.tagRepository = tagRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // Only seed if no users exist yet (first-time startup)
        if (userRepository.count() > 0) {
            System.out.println("[DataSeeder] Database already seeded, skipping.");
            return;
        }

        System.out.println("[DataSeeder] Seeding initial demo data...");

        // Create demo user
        User demo = new User("demo", "demo@notes.local", "Demo User", sha256("123456"));
        userRepository.save(demo);

        // Create tags
        Tag design = tagRepository.save(new Tag("design"));
        Tag planning = tagRepository.save(new Tag("planning"));
        Tag team = tagRepository.save(new Tag("team"));

        // Create sample notes
        Note note1 = new Note(demo, "Launch planning",
                "Kickoff notes for the new collaboration tool.\n- Wireframes\n- Sprint goals\n- Stakeholder feedback");
        note1.setFavorite(true);
        note1.setTags(Set.of(design, planning));
        noteRepository.save(note1);

        Note note2 = new Note(demo, "Design system",
                "Core tokens and component library setup.\n- Colors\n- Typography\n- Spacing scale");
        note2.setTags(Set.of(design));
        noteRepository.save(note2);

        Note note3 = new Note(demo, "Team retrospective",
                "Q2 retro highlights:\n- What went well\n- Improvements\n- Action items for next sprint");
        note3.setTags(Set.of(team));
        noteRepository.save(note3);

        System.out.println("[DataSeeder] Seeding complete. Demo user: demo / 123456");
    }

    private String sha256(String value) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
        StringBuilder builder = new StringBuilder();
        for (byte b : hash) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }
}
