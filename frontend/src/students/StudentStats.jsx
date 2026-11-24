import { useQuery } from "@tanstack/react-query"
import { apiFetch } from "../api/client"

export default function StudentStats({ studentId }) {

  const { data, isPending, isError } = useQuery({
    queryKey: ['stats', studentId],
    queryFn: () => apiFetch(`/stats?studentId=${studentId}`),
  })

  if (isPending) return <p>Loading statistics...</p>
  if (isError) return <p>Could not load statistics.</p>

  const {
    total, wins, losses, draws, winRate,
    gamesAsWhite, winsAsWhite, winRateAsWhite,
    gamesAsBlack, winsAsBlack, winRateAsBlack,
  } = data

  if (total === 0) {
    return <p>No games imported yet.</p>
  }

  return (
    <section>
      <h2>Results</h2>

      <p>
        {total} games · {wins} wins · {losses} losses · {draws} draws
      </p>
      <p>Win rate: {winRate}%</p>

      <h3>By color</h3>
      <p>White: {winsAsWhite} / {gamesAsWhite} ({winRateAsWhite}%)</p>
      <p>Black: {winsAsBlack} / {gamesAsBlack} ({winRateAsBlack}%)</p>
    </section>
  )
}