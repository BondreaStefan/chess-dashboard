import { Link } from "react-router"

export default function StudentInfoTable({ students }) {

  if (students.length === 0) {
    return <p>No students yet. Add your first student below.</p>
  }

  return (
    <table className="data-table">
      <thead>
        <tr>
          <th>Name</th>
          <th>Email</th>
          <th>Lichess Username</th>
          <th>Chess.com Username</th>
        </tr>
      </thead>
      <tbody>
        {students.map((student) => (
          <tr key={student.id}>
            <td>
              <Link to={`/students/${student.id}`}>
                {student.firstName} {student.lastName}
              </Link>
            </td>
            <td>{student.email}</td>
            <td>{student.lichessUsername ?? '-'}</td>
            <td>{student.chessComUsername ?? '-'}</td>
          </tr>
        ))}
      </tbody>
    </table>
  )
}