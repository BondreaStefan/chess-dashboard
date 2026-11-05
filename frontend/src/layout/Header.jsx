import { useAuth } from '../auth/AuthContext'
import { Link, NavLink } from 'react-router'
import './Header.css'

export function Header() {
  const { user, logout } = useAuth();

  return (
    <header className="app-header">
      <Link to="/dashboard" className="app-header__logo">Chess Dashboard</Link>

      <nav className="app-header__nav">
        <NavLink to="/dashboard">Dashboard</NavLink>
        <NavLink to="/students">Elevi</NavLink>
      </nav>

      <div className="app-header__user">
        <span>{user.email}</span>
        <button onClick={logout}>Logout</button>
      </div>
    </header>
  )
}