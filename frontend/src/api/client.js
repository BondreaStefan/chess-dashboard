export class ApiError extends Error {
  constructor(status, problem) {
    super(problem.detail ?? problem.title ?? `Request failed with status ${status}`)
    this.name = 'ApiError'
    this.status = status
    this.problem = problem
  }
}

export async function apiFetch(path, options = {}) {

  const headers = new Headers(options.headers)
  const token = localStorage.getItem('token')
  
  if(token) {
    headers.set('Authorization', `Bearer ${token}`)
  }

  if(options.body && !(options.body instanceof FormData)) {
    headers.set('Content-Type', 'application/json')
  }

  const response = await fetch(`/api/v1${path}`, {...options, headers})

  if(response.status === 401) {
    localStorage.removeItem('token')
    window.dispatchEvent(new Event('auth:unauthorized'))
  }

  if(!response.ok) {
    const problem = await response.json().catch(() => ({
      status: response.status,
      detail: response.status >= 500 ? 'Server unavailable, try again later' : undefined
    }))
    throw new ApiError(response.status, problem)
  }

  if (response.status === 204) {
    return undefined
  }

  return response.json()

}