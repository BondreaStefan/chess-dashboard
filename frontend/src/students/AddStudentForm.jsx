import { useMutation, useQueryClient } from '@tanstack/react-query'
import { apiFetch, ApiError } from "../api/client"
import { useState } from 'react'

const emptyForm = {
  firstName: '',
  lastName: '',
  email: '',
  lichessUsername: '',
  chessComUsername: '',
}

export default function AddStudentForm({ onClose }) {

  const [form, setForm] = useState(emptyForm);
  const queryClient = useQueryClient()

  function update(field, value) {
    setForm((prev) => ({ ...prev, [field]: value }));
  }

  const mutation = useMutation({
    mutationFn: (request) => apiFetch('/students', {
      method: 'POST',
      body: JSON.stringify(request)
    }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['students'] })
      queryClient.invalidateQueries({ queryKey: ['dashboard'] })
      onClose()
    },
  })

  function handleSubmit(e) {
    e.preventDefault();
    mutation.mutate({
      firstName: form.firstName,
      lastName: form.lastName,
      email: form.email,
      lichessUsername: form.lichessUsername || undefined,
      chessComUsername: form.chessComUsername || undefined,
    });
  }

  const apiError = mutation.error instanceof ApiError ? mutation.error : null;
  const fieldErrors = apiError?.problem.errors;

  return (
    <form onSubmit={handleSubmit}>
      <h2>Adaugă elev</h2>

      <label>
        Prenume
        <input
          required
          value={form.firstName}
          onChange={(e) => update('firstName', e.target.value)}
        />
      </label>
      {fieldErrors?.firstName && <span>{fieldErrors.firstName}</span>}

      <label>
        Nume
        <input
          required
          value={form.lastName}
          onChange={(e) => update('lastName', e.target.value)}
        />
      </label>
      {fieldErrors?.lastName && <span>{fieldErrors.lastName}</span>}

      <label>
        Email
        <input
          type="email"
          required
          value={form.email}
          onChange={(e) => update('email', e.target.value)}
        />
      </label>
      {fieldErrors?.email && <span>{fieldErrors.email}</span>}

      <label>
        Lichess (opțional)
        <input
          value={form.lichessUsername}
          onChange={(e) => update('lichessUsername', e.target.value)}
        />
      </label>
      {fieldErrors?.lichessUsername && (
        <span>{fieldErrors.lichessUsername}</span>
      )}

      <label>
        Chess.com (opțional)
        <input
          value={form.chessComUsername}
          onChange={(e) => update('chessComUsername', e.target.value)}
        />
      </label>
      {fieldErrors?.chessComUsername && (
        <span>{fieldErrors.chessComUsername}</span>
      )}

      <div className="add-student-form__actions">
        <button type="button" disabled={mutation.isPending} onClick={onClose}>
          Cancel
        </button>
        <button type="submit" disabled={mutation.isPending}>
          {mutation.isPending ? 'Saving...' : 'Add Student'}
        </button>
      </div>

      {apiError && !fieldErrors && <p>{apiError.message}</p>}
    </form>
  );
}