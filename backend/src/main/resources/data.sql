INSERT INTO users (username, email, display_name, password_hash, created_at)
VALUES ('demo', 'demo@notes.local', 'Demo User', '8d969eef6ecad3c29a3a629280e686cff8cae57b3b9e85b8ad1b78dedaadb4c6', NOW())
ON CONFLICT DO NOTHING;

INSERT INTO tags (name) VALUES ('design') ON CONFLICT DO NOTHING;
INSERT INTO tags (name) VALUES ('planning') ON CONFLICT DO NOTHING;
INSERT INTO tags (name) VALUES ('team') ON CONFLICT DO NOTHING;

INSERT INTO notes (owner_id, title, content, favorite, updated_at, created_at)
VALUES (1, 'Launch planning', 'Kickoff notes for the new collaboration tool.\n- Wireframes\n- Sprint goals\n- Stakeholder feedback', true, NOW(), NOW())
ON CONFLICT DO NOTHING;

INSERT INTO note_tags (note_id, tag_id) VALUES (1, 1) ON CONFLICT DO NOTHING;
INSERT INTO note_tags (note_id, tag_id) VALUES (1, 2) ON CONFLICT DO NOTHING;

INSERT INTO comments (note_id, author_id, text, created_at)
VALUES (1, 1, 'This note is ready for review. Let&apos;s refine the share workflow.', NOW())
ON CONFLICT DO NOTHING;
