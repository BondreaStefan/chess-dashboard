import { createContext, useState, useEffect, useContext } from "react";
import { apiFetch } from "../api/client";

const AuthContext = createContext(null)

function userFromToken(token) {
  if(!token)
    return null

  try {
    const payload = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/')
    const claims = JSON.parse(atob(payload))

    if(claims.exp * 1000 < Date.now())
      return null

    return {id: claims.id, email: claims.sub, role: claims.role}
  } catch {
    return null
  }
}

export function AuthProvider({ children }) {

  const [user, setUser] = useState(() => userFromToken(localStorage.getItem('token')))

  useEffect(() => {
    const handleUnauthorized = () => setUser(null)
    window.addEventListener('auth:unauthorized', handleUnauthorized)
      return () => window.removeEventListener('auth:unauthorized', handleUnauthorized)
  }, [])

  async function login(email, password) {

    const data = await apiFetch('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ email, password })
    })

    localStorage.setItem('token', data.token)
    setUser({id: data.coachId, email: data.email, role: data.role})
  }

  function logout() {
    localStorage.removeItem('token')
    setUser(null)
  }

  return (
    <AuthContext value={{ user, login, logout }}>{children}</AuthContext> 
  )
}

export function useAuth() {
  const context = useContext(AuthContext)
  if(!context) {
    throw new Error('useAuth must be used inside AuthProvider')
  }
  return context
}