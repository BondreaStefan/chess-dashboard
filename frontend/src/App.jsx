import './App.css'
import LoginPage from './auth/LoginPage';
import { Navigate, Routes, Route } from 'react-router'
import DashboardPage from './dashboard/DashboardPage';

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/dashboard" element={<DashboardPage />} />
      <Route path='*' element={<Navigate to="/login" replace />}/>
    </Routes>
  )
}
