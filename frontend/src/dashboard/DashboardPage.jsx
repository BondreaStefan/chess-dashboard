import { apiFetch } from "../api/client"
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'

export default function DashboardPage() {

  const { data, isPending, isError, error } = useQuery({
    queryKey: ['dashboard'],
    queryFn: () => apiFetch('/dashboard'),
  })

  if (isPending) 
    return <div>Loading...</div>
  if (isError) 
    return <div>Error: {error.message}</div>

  return (
    <div>
      <h1>Dashboard</h1>
      <pre>{JSON.stringify(data, null, 2)}</pre>
    </div>
  )
}