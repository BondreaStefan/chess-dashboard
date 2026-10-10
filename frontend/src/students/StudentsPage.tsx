import { useAuth } from "../auth/AuthContext";

export default function StudentsPage() {
    const { user, logout } = useAuth();

    return (
      <div>
        <h1>Elevii mei</h1>
        <p>Autentificat ca {user?.email}</p>
        <button onClick={logout}>Logout</button>
      </div>
    );
}