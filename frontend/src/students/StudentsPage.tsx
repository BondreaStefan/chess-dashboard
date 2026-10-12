import { useQuery } from '@tanstack/react-query';
import { apiFetch } from '../api/client';
import type { Student } from './types';
import AddStudentForm from './AddStudentForm';

export default function StudentsPage() {
  const { data: students, isPending, isError, error } = useQuery({
    queryKey: ['students'],
    queryFn: () => apiFetch<Student[]>('/students'),
  });

  if (isPending) return <p>Se încarcă...</p>;
  if (isError) return <p style={{ color: 'red' }}>{error.message}</p>;

  return (
    <div>
      <h1>Elevii mei</h1>
      <AddStudentForm />
      <ul>
        {students.map((student) => (
          <li key={student.id}>
            {student.firstName} {student.lastName} — {student.email}
          </li>
        ))}
      </ul>
    </div>
  );
}