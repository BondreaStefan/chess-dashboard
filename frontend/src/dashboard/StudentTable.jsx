import StudentRow from './StudentRow'

import './StudentTable.css'

export default function StudentTable({ students }) {
  if(students.length === 0) {
    return <div>No students found.</div>
  }

  return (
    <table className="data-table">
      <thead>
        <tr>
          <th>Name</th>
          <th>Games (14d)</th>
          <th>Activity</th>
          <th>Rating</th>
          <th>Last Game</th>
          <th>Rating Chart</th>
        </tr>
      </thead>
      <tbody>
        {students.map((student) => (
          <StudentRow key={student.id} student={student} />
        ))}
      </tbody>
    </table>
  )
}