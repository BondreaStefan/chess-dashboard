import { useState } from 'react'

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
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({email, password})
    };

    try {
      const response = await fetch('/api/v1/auth/login', requestOptions)
      const data = await response.json()
      if (!response.ok) {
        setError(data.detail ?? 'Login failed')
        return
      }

      localStorage.setItem('token', data.token)

    } catch {
      setError('Cannot reach the server')
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
            <p>
              {error}
            </p>
          </form>
        </div>
    )
}