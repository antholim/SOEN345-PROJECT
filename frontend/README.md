# Frontend

This is the Vite React frontend for the ticket reservation application.

## Local development

- The frontend dev server runs on `http://127.0.0.1:5173`
- The Vite proxy forwards `/api` requests to `http://127.0.0.1:8081`
- The backend should be started from the repository root with `.\start-dev.ps1`

## Production deployment

Option 1: call the backend directly from the browser by setting `VITE_API_BASE_URL` in Vercel:

```bash
VITE_API_BASE_URL=https://your-backend.example.com
```

Option 2: leave `VITE_API_BASE_URL` empty and use same-origin `/api` requests. In this repository, Vercel rewrites `/api/*` to the Railway backend, which avoids browser CORS issues because the browser only talks to the Vercel domain.
