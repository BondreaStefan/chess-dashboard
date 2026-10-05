import { useState } from 'react';

type AuthResponse = {
    token: string;
    coachId: number;
    email: string;
    role: 'ADMIN' | 'COACH';
}

export default function LoginPage() {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState<string | null>(null);
    const [auth, setAuth] = useState<AuthResponse | null>(null);
    const [loading, setLoading] = useState(false);

    async function handleSubmit(e: React.SubmitEvent<HTMLFormElement>) {
        e.preventDefault();
        setError(null);
        setLoading(true);

        try {
            const response = await fetch('/api/v1/auth/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email, password }),
            });
            const data = await response.json();

            if (!response.ok) { 
                setError(data.message ?? 'Login failed');
                return;
            }

            localStorage.setItem('token', data.token);
            setAuth(data);
        } catch {
            setError('Cannot reach the server.');
        } finally {
            setLoading(false);
        }
    }

    return (
        <div>
            <h1>Login</h1>
            <form onSubmit={handleSubmit}>
                <label>Email
                    <input value={email} onChange={(e) => setEmail(e.target.value)} />
                </label>
                <label>Password
                    <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} />
                </label>
                <button type="submit" disabled={loading}>
                    {loading ? 'Logging in...' : 'Login'}
                </button>
                {error && <p style={{ color: 'red' }}>{error}</p>}
                {auth && <p style={{ color: 'green' }}>Logged in as {auth.email} {auth.role}</p>}
            </form>
        </div>
    )
}