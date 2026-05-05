import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { login } from '../services/api';

export default function Login() {
  const navigate = useNavigate();
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    const user = await login(username, password);
    if (user) {
      localStorage.setItem('user', JSON.stringify(user));
      window.location.href = '/';
    } else {
      setError('Invalid username or password');
      setLoading(false);
    }
  };

  return (
    <div className="flex min-h-screen items-center justify-center bg-surface px-4 py-12 sm:px-6 lg:px-8">
      <div className="w-full max-w-md space-y-8 rounded-[40px] bg-white p-10 shadow-soft">
        <div>
          <div className="mx-auto h-16 w-16 rounded-3xl bg-primary flex items-center justify-center shadow-lg shadow-primary/30">
            <svg className="h-10 w-10 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
            </svg>
          </div>
          <h2 className="mt-6 text-center text-3xl font-bold tracking-tight text-slate-900">Sign in to your account</h2>
          <p className="mt-2 text-center text-sm text-slate-600">
            Use your credentials to access Harmony Notes
          </p>
        </div>
        <form className="mt-8 space-y-6" onSubmit={handleSubmit}>
          {error && (
            <div className="rounded-2xl bg-red-50 p-4 text-sm text-red-600">
              {error}
            </div>
          )}
          <div className="space-y-4 rounded-md shadow-sm">
            <div>
              <label className="text-sm font-semibold text-slate-700 ml-1">Username</label>
              <input
                type="text"
                required
                className="relative block w-full mt-2 rounded-2xl border border-slate-200 bg-slate-50 px-4 py-4 text-slate-900 outline-none transition focus:border-primary focus:ring-2 focus:ring-primary/20"
                placeholder="demo"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
              />
            </div>
            <div>
              <label className="text-sm font-semibold text-slate-700 ml-1">Password</label>
              <input
                type="password"
                required
                className="relative block w-full mt-2 rounded-2xl border border-slate-200 bg-slate-50 px-4 py-4 text-slate-900 outline-none transition focus:border-primary focus:ring-2 focus:ring-primary/20"
                placeholder="••••••••"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
              />
            </div>
          </div>

          <div>
            <button
              type="submit"
              disabled={loading}
              className="group relative flex w-full justify-center rounded-3xl bg-primary px-4 py-4 text-sm font-semibold text-white shadow-soft transition hover:bg-indigo-500 focus:outline-none disabled:opacity-70"
            >
              {loading ? 'Signing in...' : 'Sign in'}
            </button>
          </div>
          <div className="text-center text-sm text-slate-500 mt-4 space-y-2">
            <p>Don't have an account? <Link to="/signup" className="font-semibold text-primary hover:text-indigo-500">Sign up</Link></p>
            <p>Demo credentials: <strong>demo / 123456</strong></p>
          </div>
        </form>
      </div>
    </div>
  );
}
