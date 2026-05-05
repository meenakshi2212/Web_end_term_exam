import { NoteType } from '../types';

type Props = {
  note: NoteType;
  onOpen: (note: NoteType) => void;
};

export default function NoteCard({ note, onOpen }: Props) {
  return (
    <button onClick={() => onOpen(note)} className="group rounded-[28px] border border-slate-200/70 bg-white p-6 text-left transition hover:-translate-y-0.5 hover:shadow-soft">
      <div className="flex items-center justify-between gap-4">
        <h3 className="text-xl font-semibold text-slate-900">{note.title}</h3>
        <span className="rounded-full bg-slate-100 px-3 py-1 text-xs text-slate-600">{new Date(note.updatedAt).toLocaleDateString()}</span>
      </div>
      <p className="mt-4 line-clamp-3 text-sm leading-6 text-slate-600">{note.content || 'Write your first note to start your knowledge base.'}</p>
      <div className="mt-5 flex flex-wrap gap-2">
        {note.tags.map((tag) => (
          <span key={tag.id} className="rounded-full bg-slate-100 px-3 py-1 text-xs text-slate-600">#{tag.name}</span>
        ))}
      </div>
    </button>
  );
}
