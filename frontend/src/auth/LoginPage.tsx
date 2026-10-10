import { useState } from 'react';
import { ApiError } from '../api/client';
import { Navigate, useNavigate } from 'react-router';
import { useAuth } from './AuthContext';

export default function LoginPage() {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState<string | null>(null);
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();
    const { user, login } = useAuth();

    if (user) {
        return <Navigate to="/students" replace />;
    }

    async function handleSubmit(e: React.SubmitEvent<HTMLFormElement>) {
        e.preventDefault();
        setError(null);
        setLoading(true);

        try {
            await login(email, password);
            navigate('/students');
        } catch (err) {
            setError(err instanceof ApiError ? err.message : 'Cannot reach the server');
        } finally {
            setLoading(false);
        }
    }

    return (
        <div>
            <h1>Login</h1>
            <form onSubmit={handleSubmit}>
                <label>Email
                    <input type="email" required value={email} onChange={(e) => setEmail(e.target.value)} />
                </label>
                <label>Password
                    <input type="password" required value={password} onChange={(e) => setPassword(e.target.value)} />
                </label>
                <button type="submit" disabled={loading}>
                    {loading ? 'Logging in...' : 'Login'}
                </button>
                {error && <p style={{ color: 'red' }}>{error}</p>}
            </form>
        </div>
    )
}