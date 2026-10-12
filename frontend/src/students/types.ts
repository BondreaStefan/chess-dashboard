export type Student = {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  coachId?: number;
  lichessUsername?: string;
  chessComUsername?: string;
  createdAt: string;
  updatedAt?: string;
};

export type CreateStudentRequest = {
  firstName: string;
  lastName: string;
  email: string;
  lichessUsername?: string;
  chessComUsername?: string;
};