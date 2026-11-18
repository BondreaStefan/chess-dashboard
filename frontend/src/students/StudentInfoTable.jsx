
export default function StudentInfoTable({ students }) {

  if (students.length === 0) {
    return <p>No students yet. Add your first student below.</p>
  }

  return(
    <table className="student-table">
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
            <td>{student.firstName} {student.lastName}</td>
            <td>{student.email}</td>
            <td>{student.lichessUsername ?? '-'}</td>
            <td>{student.chessComUsername ?? '-'}</td>
          </tr>
        ))}
      </tbody>
    </table>
  )
}