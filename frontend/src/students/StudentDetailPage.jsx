import { useParams } from "react-router"
import { useQuery } from "@tanstack/react-query"
import { apiFetch, ApiError } from "../api/client"
import { Link } from "react-router"

export default function StudentDetailPage() {

  const { id } = useParams()
  const studentId = Number(id)

  const { data, isPending, isError, error } = useQuery({
      queryKey: ['students', studentId],
      queryFn: () => apiFetch(`/students/${id}`),
    })

  if (isPending)
    return <div>Loading...</div>
  if (isError) {
    if(error instanceof ApiError && error.status === 404) {
      return (
        <div>
          <p>This student doesn't exist</p>
          <Link to="/students">Back to students</Link>
        </div>
      )
    }
    return <div>Error: {error.message}</div>
  }

  return (
    <div>
      <h1>{data.firstName} {data.lastName}</h1>
      <p>
        Email: {data.email} Lichess username: {data.lichessUsername ?? '-'} Chess.com username: {data.chessComUsername ?? '-'}
      </p>
    </div>
  )
}