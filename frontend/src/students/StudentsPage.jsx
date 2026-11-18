import { apiFetch } from "../api/client"
import { useQuery } from '@tanstack/react-query'
import StudentInfoTable from './StudentInfoTable'

export default function StudentsPage() {

  const { data, isPending, isError, error } = useQuery({
    queryKey: ['students'],
    queryFn: () => apiFetch('/students'),
  })

  if (isPending) 
    return <div>Loading...</div>
  if (isError) 
    return <div>Error: {error.message}</div>

  return (
    <div>
      <h1>Students</h1>
      <StudentInfoTable students={data} />
    </div>
  )
}