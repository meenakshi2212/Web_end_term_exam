import { useNavigate } from 'react-router-dom';
import { NoteType } from '../types';
import NoteCard from '../components/NoteCard';

type Props = {
  notes: NoteType[];
  setSelectedNote: (note: NoteType) => void;
  onCreateNote: () => void;
};

export default function Dashboard({ notes, setSelectedNote, onCreateNote }: Props) {
  const navigate = useNavigate();

  const openNote = (note: NoteType) => {
    setSelectedNote(note);
    navigate(`/note/${note.id}`);
  };

  return (
    <div className="space-y-6">
      <section className="flex flex-wrap items-center justify-between gap-4">
        <div>
          <p className="text-sm uppercase tracking-[0.24em] text-slate-400">Your workspace</p>
          <h2 className="text-4xl font-semibold text-slate-950">Notes overview</h2>
          <p className="mt-2 text-slate-500">Focus on your ideas, organize with tags, and collaborate instantly.</p>
        </div>
        <button onClick={onCreateNote} className="rounded-3xl bg-primary px-5 py-3 text-sm font-semibold text-white shadow-soft transition hover:bg-indigo-500">Create new note</button>
      </section>

      <section className="grid gap-6 md:grid-cols-2 xl:grid-cols-3">
        {notes.length === 0 ? (
          <div className="col-span-full rounded-[28px] border border-dashed border-slate-300 bg-white p-10 text-center text-slate-500">
            No notes yet. Start by creating a new note or using the search bar.
          </div>
        ) : (
          notes.map((note) => <NoteCard key={note.id} note={note} onOpen={openNote} />)
        )}
      </section>
    </div>
  );
}
