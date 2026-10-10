import { Navigate, Route, Routes } from 'react-router';
import LoginPage from './auth/LoginPage';
import StudentsPage from './students/StudentsPage';
import RequireAuth from './auth/RequireAuth';

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/students" element={
        <RequireAuth>
          <StudentsPage />
        </RequireAuth>
      } />
      <Route path="*" element={<Navigate to="/login" replace />} />
    </Routes>
  );
}