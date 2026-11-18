import { useMutation, useQueryClient } from '@tanstack/react-query'
import { apiFetch, ApiError } from "../api/client"
import { useState } from 'react'

import './AddStudentForm.css'

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
    <form className="add-student-form" onSubmit={handleSubmit}>
      <h2>Add student</h2>

      <label className="add-student-form__field">
        First name
        <input
          required
          value={form.firstName}
          onChange={(e) => update('firstName', e.target.value)}
        />
        {fieldErrors?.firstName && <span>{fieldErrors.firstName}</span>}
      </label>
      
      <label className="add-student-form__field">
        Last name
        <input
          required
          value={form.lastName}
          onChange={(e) => update('lastName', e.target.value)}
        />
        {fieldErrors?.lastName && <span>{fieldErrors.lastName}</span>}
      </label>
      
      <label className="add-student-form__field add-student-form__field--wide">
        Email
        <input
          type="email"
          required
          value={form.email}
          onChange={(e) => update('email', e.target.value)}
        />
        {fieldErrors?.email && <span>{fieldErrors.email}</span>}
      </label>

      <label className="add-student-form__field">
        Lichess username (optional)
        <input
          value={form.lichessUsername}
          onChange={(e) => update('lichessUsername', e.target.value)}
        />
        {fieldErrors?.lichessUsername && (
          <span>{fieldErrors.lichessUsername}</span>
        )}
      </label>
      
      <label className="add-student-form__field">
        Chess.com username (optional)
        <input
          value={form.chessComUsername}
          onChange={(e) => update('chessComUsername', e.target.value)}
        />
        {fieldErrors?.chessComUsername && (
          <span>{fieldErrors.chessComUsername}</span>
        )}
      </label>

      <div className="add-student-form__actions">
        <button type="button" className="button button--secondary" disabled={mutation.isPending} onClick={onClose}>
          Cancel
        </button>
        <button type="submit" className="button button--primary" disabled={mutation.isPending}>
          {mutation.isPending ? 'Saving...' : 'Add Student'}
        </button>
      </div>

      {apiError && !fieldErrors && <p className="add-student-form__message">{apiError.message}</p>}
    </form>
  );
}