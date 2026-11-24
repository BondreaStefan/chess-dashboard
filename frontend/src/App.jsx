import './App.css'
import LoginPage from './auth/LoginPage'
import { Navigate, Routes, Route } from 'react-router'
import DashboardPage from './dashboard/DashboardPage'
import StudentsPage from './students/StudentsPage'
import StudentDetailPage from './students/StudentDetailPage'
import RequireAuth from './auth/RequireAuth'
import Layout from './layout/Layout'

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route
        element={
          <RequireAuth>
            <Layout />
          </RequireAuth>
        }
      >
        <Route path="/dashboard" element={<DashboardPage />} />
        <Route path="/students" element={<StudentsPage />} />
        <Route path="/students/:id" element={<StudentDetailPage />} />
      </Route>
      <Route path='*' element={<Navigate to="/login" replace />}/>
    </Routes>
  )
}
