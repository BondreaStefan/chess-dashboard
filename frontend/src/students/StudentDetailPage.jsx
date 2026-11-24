import { useParams } from "react-router"
import { useQuery } from "@tanstack/react-query"
import { apiFetch, ApiError } from "../api/client"
import { Link } from "react-router"
import StudentStats from "./StudentStats"
import ProfileHeader from "./ProfileHeader"
import GamesList from "./GamesList"

export default function StudentDetailPage() {

  const { id } = useParams()
  const studentId = Number(id)

  const { data, error, isError, isPending } = useQuery({
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
      <ProfileHeader student={data}/>
      <StudentStats studentId={studentId}/>
      <GamesList studentId={studentId} />
    </div>
  )
}