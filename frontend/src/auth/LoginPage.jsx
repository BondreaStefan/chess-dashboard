import { useState } from 'react'
import { ApiError, apiFetch } from '../api/client'

export default function LoginPage() {

  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState(null)
  const [loading, setLoading] = useState(false)

  async function handleSubmit(e) {
    e.preventDefault()
    setLoading(true)
    setError(null)

    const requestOptions = {
      method: 'POST',
      body: JSON.stringify({email, password})
    };

    try {
      const data = await apiFetch('/auth/login', requestOptions)
      localStorage.setItem('token', data.token)

    } catch(err) {
      setError(err instanceof ApiError ? err.message :'Cannot reach the server')
    } finally {
      setLoading(false)
    }
  }

  return (
        <div>
          <h1>Login</h1>
          <form onSubmit={handleSubmit}>
            <input
              type="email"
              placeholder="Email"
              value={email}
              required
              onChange= {(e) => setEmail(e.target.value)}
            />
            <input
              type="password"
              placeholder="Password"
              value={password}
              required
              onChange= {(e) => setPassword(e.target.value)} 
            />
            <button type="submit" disabled={loading}>
              {loading ? 'Logging in..' : 'Login'}
            </button>
            {error && <p>{error}</p>}
          </form>
        </div>
    )
}