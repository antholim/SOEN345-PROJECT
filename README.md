# Group Project: Cloud-based Ticket Reservation Application  
## PROJECT DESCRIPTION:  
In this course students are required to work on a software development  
and testing project and write a professional high-quality report.  
The main objection of the application is to develop a ticket booking for events such as movies, concerts, travel, or sports.  
The Ticket Reservation Application allows users to browse events, reserve tickets, and receive confirmations digitally.  
The programming language used in this project is Java.  
For frontend, we have used React
For backend, we have used Gradle and PostgreSQL
## INTENDED USERS
a. Customers (end users)  
b. Event organizers / administrators  

## FUNCTIONAL REQUIREMENTS
Users should be able to:  
a. register using email or phone number  
b. view a list of available events  
c. search and filter events by date, location, or category  
d. cancel reservations  
e. receive confirmations via email or SMS  
### Administrators should be able to:  
a. add new event  
b. edit an existing event  
c. cancel an event  
## NON-FUNCTIONAL REQUIREMENTS  
a. The system should support concurrent users without performance degradation  
b. The system should be cloud based that ensures high availability  
c. The UI should be simple and user-friendly  

## Run Locally

### Simplest Option

1. Copy the example environment file:
   `Copy-Item .env.dev.example.ps1 .env.dev.ps1`
2. Put the real database values into `.env.dev.ps1`
3. From the project root, run:
   `.\start-dev.ps1`

This opens:
- frontend on `http://127.0.0.1:5173`
- backend on `http://127.0.0.1:8081`

### Requirements

- Java 23
- Node.js and npm

## Deploying To Vercel

This repository is split into:
- `frontend/`: Vite React app
- `backend/`: Spring Boot API

Vercel should build and serve the frontend only. The root `vercel.json` points Vercel at `frontend/` so deployments from the repository root produce the static site from `frontend/dist`.

The Spring Boot backend is not deployed by this Vercel configuration and should be hosted separately. Vercel does not run this repository's Dockerized Spring Boot server as part of the current setup.

### Production setup

1. Deploy the backend somewhere that can run Spring Boot and PostgreSQL-backed workloads.
2. If you want the browser to call the backend directly, set `VITE_API_BASE_URL` in Vercel to that backend URL, for example:
   `https://your-backend.example.com`
3. In the backend environment, set `APP_CORS_ALLOWED_ORIGINS` to the frontend origin or origins, for example:
   `https://soen345project.vercel.app,https://your-preview-domain.vercel.app`

When `VITE_API_BASE_URL` is not set, the frontend uses relative `/api` paths. That is useful for local development with the Vite proxy, but it is not enough for production unless an API is actually mounted on the same domain.

This repository now includes a Vercel rewrite from `/api/*` to the Railway backend. That means the simplest production setup is to leave `VITE_API_BASE_URL` unset in Vercel so the browser calls `/api/...` on the same Vercel domain and Vercel proxies those requests to Railway.
