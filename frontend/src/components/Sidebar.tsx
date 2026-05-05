import { TagType } from '../types';

type Props = {
  tags: TagType[];
  activeTag: string;
  onTagSelect: (tag: string) => void;
  onCreateNote: () => void;
};

export default function Sidebar({ tags, activeTag, onTagSelect, onCreateNote }: Props) {
  return (
    <aside className="hidden w-80 flex-col gap-6 rounded-[28px] bg-slate-950/95 p-6 text-white shadow-soft xl:flex">
      <div>
        <div className="mb-6 text-sm font-semibold uppercase tracking-[0.24em] text-slate-400">Notes Lab</div>
        <h1 className="text-3xl font-semibold text-white">Harmony Notes</h1>
        <p className="mt-3 text-sm text-slate-300">Modern note management for teams, tags, and real-time updates.</p>
      </div>

      <div className="space-y-3 rounded-3xl bg-slate-900/80 p-4">
        <button onClick={() => onTagSelect('All')} className={`w-full rounded-2xl px-4 py-3 text-left transition ${activeTag === 'All' ? 'bg-primary/90 text-white' : 'bg-slate-800 text-slate-300 hover:bg-slate-700'}`}>
          All Notes
        </button>
        <button className="w-full rounded-2xl px-4 py-3 text-left bg-slate-800 text-slate-300 hover:bg-slate-700">Shared with Me</button>
        <button className="w-full rounded-2xl px-4 py-3 text-left bg-slate-800 text-slate-300 hover:bg-slate-700">My Groups</button>
      </div>

      <div className="rounded-3xl bg-slate-900/80 p-4">
        <div className="mb-4 text-sm font-semibold uppercase tracking-[0.24em] text-slate-400">Tags</div>
        <div className="space-y-2">
          {tags.map((tag) => (
            <button key={tag.id} onClick={() => onTagSelect(tag.name)} className={`w-full rounded-2xl px-4 py-3 text-left text-sm transition ${activeTag === tag.name ? 'bg-primary/90 text-white' : 'bg-slate-800 text-slate-300 hover:bg-slate-700'}`}>
              #{tag.name}
            </button>
          ))}
        </div>
      </div>

      <button onClick={onCreateNote} className="mt-auto rounded-3xl bg-white px-5 py-4 text-slate-950 shadow-soft transition hover:-translate-y-0.5 hover:bg-slate-100">
        + Create note
      </button>
    </aside>
  );
}
