import { useState } from 'react'
import { ApiError } from '../api/client'
import { useAuth } from './AuthContext'
import { useNavigate } from 'react-router'

export default function LoginPage() {

  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState(null)
  const [loading, setLoading] = useState(false)
  const navigate = useNavigate()
  const { user, login } = useAuth()

  async function handleSubmit(e) {
    e.preventDefault()
    setLoading(true)
    setError(null)

    try {
      await login(email, password)
      navigate('/dashboard')

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