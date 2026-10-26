import './App.css'
import LoginPage from './auth/LoginPage';
import { Navigate, Routes, Route } from 'react-router'
import DashboardPage from './dashboard/DashboardPage';
import RequireAuth from './auth/RequireAuth';

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/dashboard" element={
        <RequireAuth>
          <DashboardPage />
        </RequireAuth>
        } 
      />
      <Route path='*' element={<Navigate to="/login" replace />}/>
    </Routes>
  )
}
