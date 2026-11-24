import { useState } from 'react'
import { keepPreviousData, useQuery } from '@tanstack/react-query'
import { apiFetch } from '../api/client'

const PAGE_SIZE = 20

export default function GamesList({ studentId }) {
  const [page, setPage] = useState(0)

  const { data, isPending, isError, isPlaceholderData } = useQuery({
    queryKey: ['games', studentId, page],
    queryFn: () => apiFetch(`/games?studentId=${studentId}&page=${page}&size=${PAGE_SIZE}`),
    placeholderData: keepPreviousData,
  })

  if (isPending) return <p>Loading games...</p>
  if (isError) return <p>Could not load games.</p>

  const { content: games, page: pageInfo } = data

  if (pageInfo.totalElements === 0) {
    return <p>No games imported yet.</p>
  }

  return (
    <section>
      <h2>Games</h2>
      <table className="data-table">
        <thead>
          <tr>
            <th>Date</th>
            <th>Opponent</th>
            <th>Color</th>
            <th>Result</th>
            <th>Type</th>
            <th>Moves</th>
          </tr>
        </thead>
        <tbody>
          {games.map((game) => (
            <tr key={game.id}>
              <td>{game.playedAt ? new Date(game.playedAt).toLocaleDateString() : '—'}</td>
              <td>{game.opponentName ?? '—'}</td>
              <td>{game.studentColor}</td>
              <td>{game.studentResult}</td>
              <td>{game.timeClass}</td>
              <td>{game.moveCount}</td>
            </tr>
          ))}
        </tbody>
      </table>

      <div>
        <button onClick={() => setPage((p) => p - 1)} disabled={page === 0}>
          Previous
        </button>
        <span> Page {pageInfo.number + 1} of {pageInfo.totalPages} </span>
        <button
          onClick={() => setPage((p) => p + 1)}
          disabled={isPlaceholderData || page + 1 >= pageInfo.totalPages}
        >
          Next
        </button>
      </div>
    </section>
  )
}