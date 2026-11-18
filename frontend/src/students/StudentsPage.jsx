import { apiFetch } from "../api/client"
import { useQuery } from '@tanstack/react-query'
import { useState } from 'react'
import StudentInfoTable from './StudentInfoTable'
import AddStudentForm from './AddStudentForm'

import './StudentsPage.css'

export default function StudentsPage() {

  const { data, isPending, isError, error } = useQuery({
    queryKey: ['students'],
    queryFn: () => apiFetch('/students'),
  })

  const [isAdding, setIsAdding] = useState(false)

  if (isPending)
    return <div>Loading...</div>
  if (isError)
    return <div>Error: {error.message}</div>

  return (
    <div>
      <div className="students-page__header">
        <h1>Students</h1>
        {!isAdding && (
          <button className="button button--primary" onClick={() => setIsAdding(true)}>
            Add a new student
          </button>
        )}
      </div>
      
      {isAdding && <AddStudentForm onClose={() => setIsAdding(false)} />}

      <StudentInfoTable students={data} />
    </div>
  )
}