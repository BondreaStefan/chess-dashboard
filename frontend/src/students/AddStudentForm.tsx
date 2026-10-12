import { useState } from 'react';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { apiFetch, ApiError } from '../api/client';
import type { CreateStudentRequest, Student } from './types';

const emptyForm = {
  firstName: '',
  lastName: '',
  email: '',
  lichessUsername: '',
  chessComUsername: '',
};

export default function AddStudentForm() {
  const [form, setForm] = useState(emptyForm);
  const queryClient = useQueryClient();

  const mutation = useMutation({
    mutationFn: (request: CreateStudentRequest) =>
      apiFetch<Student>('/students', {
        method: 'POST',
        body: JSON.stringify(request),
      }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['students'] });
      setForm(emptyForm);
    },
  });

  function update(field: keyof typeof emptyForm, value: string) {
    setForm((prev) => ({ ...prev, [field]: value }));
  }

  function handleSubmit(e: React.SubmitEvent<HTMLFormElement>) {
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
      {fieldErrors?.firstName && <span style={{ color: 'red' }}>{fieldErrors.firstName}</span>}

      <label>
        Nume
        <input
          required
          value={form.lastName}
          onChange={(e) => update('lastName', e.target.value)}
        />
      </label>
      {fieldErrors?.lastName && <span style={{ color: 'red' }}>{fieldErrors.lastName}</span>}

      <label>
        Email
        <input
          type="email"
          required
          value={form.email}
          onChange={(e) => update('email', e.target.value)}
        />
      </label>
      {fieldErrors?.email && <span style={{ color: 'red' }}>{fieldErrors.email}</span>}

      <label>
        Lichess (opțional)
        <input
          value={form.lichessUsername}
          onChange={(e) => update('lichessUsername', e.target.value)}
        />
      </label>
      {fieldErrors?.lichessUsername && (
        <span style={{ color: 'red' }}>{fieldErrors.lichessUsername}</span>
      )}

      <label>
        Chess.com (opțional)
        <input
          value={form.chessComUsername}
          onChange={(e) => update('chessComUsername', e.target.value)}
        />
      </label>
      {fieldErrors?.chessComUsername && (
        <span style={{ color: 'red' }}>{fieldErrors.chessComUsername}</span>
      )}

      <button type="submit" disabled={mutation.isPending}>
        {mutation.isPending ? 'Se salvează...' : 'Adaugă'}
      </button>

      {apiError && !fieldErrors && <p style={{ color: 'red' }}>{apiError.message}</p>}
    </form>
  );
}