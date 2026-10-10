export type ProblemDetail = {
  title?: string;
  status: number;
  detail?: string;
  instance?: string;
  errors?: Record<string, string>;
};

export class ApiError extends Error {
  status: number;
  problem: ProblemDetail;

  constructor(status: number, problem: ProblemDetail) {
    super(problem.detail ?? problem.title ?? `Request failed with status ${status}`);
    this.status = status;
    this.problem = problem;
  }
}

export async function apiFetch<T>(path: string, options: RequestInit = {}): Promise<T> {
  const headers = new Headers(options.headers);

  const token = localStorage.getItem('token');
  if (token) {
    headers.set('Authorization', `Bearer ${token}`);
  }

  if (options.body && !(options.body instanceof FormData)) {
    headers.set('Content-Type', 'application/json');
  }

  const response = await fetch(`/api/v1${path}`, { ...options, headers });

  if (response.status === 401) {
    localStorage.removeItem('token');
  }

  if (!response.ok) {
    const problem = await response.json().catch(() => ({ 
        status: response.status,
        detail: response.status >= 500 ? 'Server unavailable, try again later' : undefined,
     }));
    throw new ApiError(response.status, problem);
  }

  if (response.status === 204) {
    return undefined as T;
  }

  return response.json() as Promise<T>;
}