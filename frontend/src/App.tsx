import { useEffect, useMemo, useState } from 'react';
import { BrowserRouter, Route, Routes, useNavigate } from 'react-router-dom';
import Dashboard from './pages/Dashboard';
import Editor from './pages/Editor';
import Login from './pages/Login';
import Signup from './pages/Signup';
import Topbar from './components/Topbar';
import Sidebar from './components/Sidebar';
import { connectRealtime } from './services/socket';
import { NoteType, TagType } from './types';
import { fetchNotes, fetchTags, createNote } from './services/api';

function Layout() {
  const navigate = useNavigate();
  const [user] = useState(() => JSON.parse(localStorage.getItem('user') || 'null'));
  const [notes, setNotes] = useState<NoteType[]>([]);
  const [tags, setTags] = useState<TagType[]>([]);
  const [search, setSearch] = useState('');
  const [activeTag, setActiveTag] = useState<string>('All');
  const [selectedNote, setSelectedNote] = useState<NoteType | null>(null);
  const [activeEditors, setActiveEditors] = useState(1);

  useEffect(() => {
    if (!user) {
      if (window.location.pathname !== '/login' && window.location.pathname !== '/signup') {
        navigate('/login');
      }
      return;
    }
    fetchNotes(user.userId).then(setNotes);
    fetchTags().then(setTags);
  }, [user, navigate]);

  useEffect(() => {
    if (!user) return;
    const disconnect = connectRealtime((payload) => {
      if (payload.activeEditors != null) {
        setActiveEditors(payload.activeEditors);
      }
    });
    return disconnect;
  }, []);

  const filteredNotes = useMemo(() => {
    return notes.filter((note) => {
      const matchesSearch = search.length === 0 || [note.title, note.content].some((value) => value.toLowerCase().includes(search.toLowerCase()));
      const matchesTag = activeTag === 'All' || note.tags.some((tag) => tag.name === activeTag);
      return matchesSearch && matchesTag;
    });
  }, [notes, search, activeTag]);

  const handleCreateNote = async () => {
    if (!user) return;
    const newNote = await createNote({ ownerId: user.userId, title: 'New Note', content: '' });
    if (newNote) {
      setNotes((prev) => [newNote, ...prev]);
      setSelectedNote(newNote);
      navigate(`/note/${newNote.id}`);
    }
  };

  if (!user && window.location.pathname !== '/login' && window.location.pathname !== '/signup') {
    return null; // Let the useEffect navigate
  }

  if (!user && (window.location.pathname === '/login' || window.location.pathname === '/signup')) {
    return (
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/signup" element={<Signup />} />
        <Route path="*" element={<Login />} />
      </Routes>
    );
  }

  return (
    <div className="min-h-screen bg-surface text-slate-900">
      <Topbar search={search} onSearch={setSearch} activeEditors={activeEditors} user={user} />
      <div className="mx-auto flex max-w-[1600px] gap-6 px-4 py-6 xl:px-10">
        <Sidebar tags={tags} activeTag={activeTag} onTagSelect={setActiveTag} onCreateNote={handleCreateNote} />
        <main className="flex-1 rounded-[32px] bg-white/90 p-6 shadow-soft backdrop-blur-xl">
          <Routes>
            <Route path="/" element={<Dashboard notes={filteredNotes} setSelectedNote={setSelectedNote} onCreateNote={handleCreateNote} />} />
            <Route path="/note/:id" element={<Editor note={selectedNote} onUpdateNotes={setNotes} />} />
            <Route path="/login" element={<Login />} />
            <Route path="/signup" element={<Signup />} />
            <Route path="*" element={<Dashboard notes={filteredNotes} setSelectedNote={setSelectedNote} onCreateNote={handleCreateNote} />} />
          </Routes>
        </main>
      </div>
    </div>
  );
}

function App() {
  return (
    <BrowserRouter>
      <Layout />
    </BrowserRouter>
  );
}

export default App;
