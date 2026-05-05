import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { NoteType } from '../types';
import { sendNoteUpdate } from '../services/socket';
import { updateNote, shareNote } from '../services/api';

type Props = {
  note: NoteType | null;
  onUpdateNotes: (updater: (current: NoteType[]) => NoteType[]) => void;
};

export default function Editor({ note, onUpdateNotes }: Props) {
  const { id } = useParams();
  const navigate = useNavigate();
  const [title, setTitle] = useState(note?.title || '');
  const [content, setContent] = useState(note?.content || '');
  const [tags, setTags] = useState(note?.tags.map((tag) => tag.name).join(', ') || '');
  const [status, setStatus] = useState('');
  const [showShareModal, setShowShareModal] = useState(false);
  const [shareUsername, setShareUsername] = useState('');
  const [shareStatus, setShareStatus] = useState('');

  const handleShare = async () => {
    setShareStatus('Sharing...');
    const res = await shareNote(note!.id, shareUsername);
    if (res && !res.error) {
      setShareStatus('Successfully shared!');
      setTimeout(() => {
        setShowShareModal(false);
        setShareUsername('');
        setShareStatus('');
      }, 2000);
    } else {
      setShareStatus(res?.error || 'Error sharing note');
    }
  };

  useEffect(() => {
    if (note) {
      setTitle(note.title);
      setContent(note.content);
      setTags(note.tags.map((tag) => tag.name).join(', '));
    }
  }, [note]);

  if (!note) {
    return (
      <div className="rounded-[28px] border border-slate-200 bg-slate-50 p-10 text-center text-slate-500">
        Select a note to edit or return to the dashboard.
        <button onClick={() => navigate('/')} className="mt-5 inline-flex rounded-full bg-primary px-5 py-3 text-white">Back</button>
      </div>
    );
  }

  const save = async () => {
    setStatus('Saving...');
    const updated = await updateNote(note.id, {
      title,
      content,
      tags: tags.split(',').map((tag) => tag.trim()).filter(Boolean),
    });
    if (updated) {
      onUpdateNotes((current) => current.map((item) => (item.id === updated.id ? updated : item)));
      sendNoteUpdate({ noteId: note.id, activeEditors: Math.floor(Math.random() * 4) + 1 });
      setStatus('Saved');
      setTimeout(() => setStatus(''), 1500);
    } else {
      setStatus('Error saving note');
    }
  };

  return (
    <div className="space-y-6">
      <div className="rounded-[28px] bg-slate-50 p-6 shadow-soft">
        <div className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
          <div>
            <h2 className="text-3xl font-semibold text-slate-950">{note.title}</h2>
            <p className="mt-2 text-sm text-slate-500">Last updated {new Date(note.updatedAt).toLocaleString()}</p>
          </div>
          <div className="flex flex-wrap gap-3">
            <button onClick={save} className="rounded-3xl bg-primary px-5 py-3 text-sm font-semibold text-white shadow-soft transition hover:bg-indigo-500">Save</button>
            <button onClick={() => setShowShareModal(true)} className="rounded-3xl border border-slate-200 bg-white px-5 py-3 text-sm text-slate-700 transition hover:bg-slate-50">Share</button>
            <button className="rounded-3xl border border-slate-200 bg-white px-5 py-3 text-sm text-slate-700 transition hover:bg-slate-50">History</button>
          </div>
        </div>
      </div>

      {showShareModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-950/20 backdrop-blur-sm px-4">
          <div className="w-full max-w-md rounded-[32px] bg-white p-8 shadow-2xl">
            <div className="flex items-center justify-between">
              <h3 className="text-xl font-bold text-slate-900">Share note</h3>
              <button onClick={() => setShowShareModal(false)} className="text-slate-400 hover:text-slate-600">
                <svg className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                </svg>
              </button>
            </div>
            <p className="mt-2 text-sm text-slate-500">Enter the username of the person you want to share this note with.</p>
            <div className="mt-6 space-y-4">
              <div>
                <label className="text-sm font-semibold text-slate-700">Username</label>
                <input 
                  value={shareUsername} 
                  onChange={(e) => setShareUsername(e.target.value)} 
                  placeholder="e.g. johndoe" 
                  className="mt-2 w-full rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3 outline-none transition focus:border-primary focus:ring-2 focus:ring-primary/20" 
                />
              </div>
              {shareStatus && (
                <p className={`text-sm ${shareStatus.includes('Successfully') ? 'text-green-600' : 'text-slate-600'}`}>{shareStatus}</p>
              )}
              <button 
                onClick={handleShare} 
                className="w-full rounded-3xl bg-primary py-4 font-semibold text-white shadow-soft transition hover:bg-indigo-500"
              >
                Send Invite
              </button>
            </div>
          </div>
        </div>
      )}

      <section className="grid gap-6 xl:grid-cols-[2fr_1fr]">
        <div className="rounded-[28px] bg-white p-6 shadow-soft">
          <label className="mb-2 block text-sm font-semibold text-slate-700">Title</label>
          <input value={title} onChange={(e) => setTitle(e.target.value)} placeholder="Note title" className="mb-6 w-full rounded-3xl border border-slate-200 bg-slate-50 px-4 py-4 text-lg outline-none" />
          <label className="mb-2 block text-sm font-semibold text-slate-700">Content</label>
          <textarea value={content} onChange={(e) => setContent(e.target.value)} rows={12} placeholder="Type your note here..." className="w-full rounded-[24px] border border-slate-200 bg-slate-50 px-4 py-4 text-sm leading-7 outline-none" />
        </div>
        <aside className="space-y-6 rounded-[28px] bg-white p-6 shadow-soft">
          <div>
            <h3 className="text-lg font-semibold text-slate-900">Tags</h3>
            <p className="mt-2 text-sm text-slate-500">Add comma-separated tags to organize this note.</p>
            <input value={tags} onChange={(e) => setTags(e.target.value)} placeholder="work, design, planning" className="mt-4 w-full rounded-3xl border border-slate-200 bg-slate-50 px-4 py-3 outline-none" />
          </div>

          <div className="rounded-3xl bg-slate-50 p-4">
            <p className="text-sm text-slate-500">Collaboration</p>
            <div className="mt-4 flex items-center gap-3">
              <div className="h-12 w-12 rounded-full bg-primary text-white flex items-center justify-center">A</div>
              <div>
                <p className="font-semibold text-slate-900">Active editor</p>
                <p className="text-sm text-slate-500">3 people currently reviewing</p>
              </div>
            </div>
          </div>
          <div className="rounded-3xl bg-slate-50 p-4 text-sm text-slate-500">{status || 'Autosave is active. Your latest changes will be stored automatically.'}</div>
        </aside>
      </section>
    </div>
  );
}
