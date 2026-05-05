type Props = {
  search: string;
  onSearch: (value: string) => void;
  activeEditors: number;
  user: any;
};

export default function Topbar({ search, onSearch, activeEditors, user }: Props) {
  const handleLogout = () => {
    localStorage.removeItem('user');
    window.location.href = '/login';
  };
  return (
    <header className="sticky top-0 z-20 border-b border-slate-200/70 bg-surface/95 backdrop-blur-xl">
      <div className="mx-auto flex max-w-[1600px] items-center justify-between gap-4 px-4 py-4 xl:px-10">
        <div className="flex flex-1 items-center gap-4 rounded-3xl border border-slate-200/80 bg-white/90 px-4 py-3 shadow-soft">
          <span className="text-slate-400">Search</span>
          <input
            value={search}
            onChange={(e) => onSearch(e.target.value)}
            placeholder="Search notes, tags, or text..."
            className="w-full bg-transparent text-slate-900 outline-none placeholder:text-slate-400"
          />
        </div>
        <div className="flex items-center gap-3">
          <div className="rounded-3xl bg-slate-100 px-4 py-3 text-sm text-slate-700 shadow-sm">{activeEditors} active</div>
          <button onClick={handleLogout} className="rounded-3xl border border-slate-200 bg-white px-4 py-3 text-sm text-slate-700 transition hover:bg-slate-50">Logout</button>
          <div className="flex items-center gap-3">
            <span className="hidden text-sm font-semibold text-slate-700 md:block">{user?.displayName || user?.username}</span>
            <div className="flex h-12 w-12 items-center justify-center rounded-full bg-primary text-white shadow-soft">
              {user?.displayName?.[0] || user?.username?.[0] || 'U'}
            </div>
          </div>
        </div>
      </div>
    </header>
  );
}
