import { useState } from "react"
import { useQueryClient, useMutation } from "@tanstack/react-query"
import { apiFetch } from "../api/client"

export default function ImportGames({ studentId }) {

  const [isOpen, setIsOpen] = useState(false)
  const [file, setFile] = useState(null)
  const queryClient = useQueryClient()

  const mutation = useMutation({
    mutationFn: (file) => {
      const formData = new FormData()
      formData.append('studentId', studentId)
      formData.append('file', file)
      return apiFetch('/games/import/file', { method: 'POST', body: formData })
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['games', studentId] })
      queryClient.invalidateQueries({ queryKey: ['stats', studentId] })
      queryClient.invalidateQueries({ queryKey: ['dashboard'] })
    },
  })

  function close() {
    setIsOpen(false)
    setFile(null)
    mutation.reset()
  }

  function handleSubmit(e) {
    e.preventDefault()
    mutation.mutate(file)
  }

  if (!isOpen) {
    return <button onClick={() => setIsOpen(true)}>Import games</button>
  }

  return (
    <form onSubmit={handleSubmit}>
      <h2>Import games</h2>
      <p>Upload a PGN file exported from Lichess or Chess.com. Games in which this student didn't play are skipped.</p>

      <input
        type="file"
        accept=".pgn"
        onChange={(e) => setFile(e.target.files[0] ?? null)}
      />

      <div>
        <button type="button" onClick={close} disabled={mutation.isPending}>
          Close
        </button>
        <button type="submit" disabled={!file || mutation.isPending}>
          {mutation.isPending ? 'Importing...' : 'Import'}
        </button>
      </div>

      {mutation.isSuccess && (
        <p>
          Imported {mutation.data.imported} {mutation.data.imported === 1 ? 'game' : 'games'}
          {mutation.data.skipped > 0 && `, skipped ${mutation.data.skipped}`}.
        </p>
      )}

      {mutation.isError && <p>{mutation.error.message}</p>}
    </form>
  )
}