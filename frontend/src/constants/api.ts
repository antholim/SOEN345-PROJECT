const rawApiBaseUrl = import.meta.env.VITE_API_BASE_URL?.trim() ?? ''

// In production we prefer same-origin /api requests so Vercel rewrites can proxy
// to the backend without browser-side CORS.
export const API_BASE_URL = import.meta.env.DEV
  ? rawApiBaseUrl.replace(/\/$/, '')
  : ''
