import { useState } from 'react'
import type { FormEvent } from 'react'
import './App.css'

type UserRole = 'CUSTOMER' | 'ADMIN'
type AuthMode = 'login' | 'register'

type UserRecord = {
  userId: number
  firstName: string
  lastName: string
  email: string | null
  phoneNumber: string | null
  role: UserRole
  createdAt: string
}

type DashboardMetric = {
  label: string
  value: string
  note: string
}

type DashboardAction = {
  title: string
  description: string
}

type ApiErrorResponse = {
  detail?: string
  message?: string
}

const DASHBOARD_COPY: Record<
  UserRole,
  {
    title: string
    summary: string
    metrics: DashboardMetric[]
    actions: DashboardAction[]
  }
> = {
  CUSTOMER: {
    title: 'Customer Dashboard',
    summary:
      'Review reservations, keep track of upcoming plans, and move quickly into event browsing.',
    metrics: [
      { label: 'Upcoming Trips', value: '3', note: 'Next booking this weekend' },
      { label: 'Open Reservations', value: '2', note: 'Ready to manage' },
      { label: 'Unread Confirmations', value: '1', note: 'Latest email waiting' },
    ],
    actions: [
      {
        title: 'Browse events',
        description: 'Explore new concerts, travel dates, sports, and local experiences.',
      },
      {
        title: 'Manage reservations',
        description: 'Review active bookings and make changes before confirmation deadlines.',
      },
      {
        title: 'View confirmations',
        description: 'Check email and SMS confirmations for recent reservations.',
      },
    ],
  },
  ADMIN: {
    title: 'Admin Dashboard',
    summary:
      'Monitor event activity, manage inventory, and keep venue operations organized from one place.',
    metrics: [
      { label: 'Active Events', value: '12', note: '4 need review today' },
      { label: 'Venues Managed', value: '5', note: '2 at high capacity' },
      { label: 'Pending Changes', value: '7', note: 'Awaiting approval' },
    ],
    actions: [
      {
        title: 'Create an event',
        description: 'Add a new listing with schedule, venue, pricing, and ticket limits.',
      },
      {
        title: 'Edit live events',
        description: 'Update timing, pricing, and availability without leaving the dashboard.',
      },
      {
        title: 'Review reservations',
        description: 'Track customer demand and handle cancellations or operational issues.',
      },
    ],
  },
}

const API_BASE_URL = (import.meta.env.VITE_API_BASE_URL ?? '').replace(/\/$/, '')

const getFullName = (user: UserRecord) => `${user.firstName} ${user.lastName}`
const getApiUrl = (path: string) => (API_BASE_URL ? `${API_BASE_URL}${path}` : path)

function App() {
  const [mode, setMode] = useState<AuthMode>('login')
  const [loginIdentifier, setLoginIdentifier] = useState('')
  const [loginPassword, setLoginPassword] = useState('')
  const [registrationForm, setRegistrationForm] = useState({
    firstName: '',
    lastName: '',
    email: '',
    phoneNumber: '',
    password: '',
  })
  const [showPassword, setShowPassword] = useState(false)
  const [currentUser, setCurrentUser] = useState<UserRecord | null>(null)
  const [error, setError] = useState('')
  const [successMessage, setSuccessMessage] = useState('')
  const [isSubmitting, setIsSubmitting] = useState(false)

  const handleLoginSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()

    if (isSubmitting) {
      return
    }

    setError('')
    setSuccessMessage('')
    setIsSubmitting(true)

    try {
      const response = await fetch(getApiUrl('/api/auth/login'), {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          email: loginIdentifier,
          password: loginPassword,
        }),
      })

      if (!response.ok) {
        const errorResponse = (await response.json().catch(() => null)) as ApiErrorResponse | null
        setCurrentUser(null)
        setError(
          errorResponse?.detail ??
            errorResponse?.message ??
            'We could not sign you in right now.',
        )
        return
      }

      const user = (await response.json()) as UserRecord
      setCurrentUser(user)
      setError('')
    } catch {
      setCurrentUser(null)
      setError('The backend is unavailable. Start the Spring server and try again.')
    } finally {
      setIsSubmitting(false)
    }
  }

  const handleRegisterSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()

    if (isSubmitting) {
      return
    }

    setError('')
    setSuccessMessage('')
    setIsSubmitting(true)

    try {
      const response = await fetch(getApiUrl('/api/auth/register'), {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(registrationForm),
      })

      if (!response.ok) {
        const errorResponse = (await response.json().catch(() => null)) as ApiErrorResponse | null
        setError(
          errorResponse?.detail ??
            errorResponse?.message ??
            'We could not create your account right now.',
        )
        return
      }

      const user = (await response.json()) as UserRecord
      setCurrentUser(user)
      setRegistrationForm({
        firstName: '',
        lastName: '',
        email: '',
        phoneNumber: '',
        password: '',
      })
      setLoginIdentifier(user.email ?? user.phoneNumber ?? '')
      setLoginPassword('')
      setSuccessMessage('Account created successfully.')
    } catch {
      setError('The backend is unavailable. Start the Spring server and try again.')
    } finally {
      setIsSubmitting(false)
    }
  }

  const logout = () => {
    setCurrentUser(null)
    setLoginPassword('')
    setShowPassword(false)
    setError('')
  }

  if (currentUser) {
    const dashboard = DASHBOARD_COPY[currentUser.role]

    return (
      <main className="dashboard-shell">
        <section className="dashboard-hero">
          <div className="dashboard-topbar">
            <div>
              <p className="eyebrow">Ticket Reservation Application</p>
              <h1>{dashboard.title}</h1>
            </div>
            <button type="button" className="secondary-button" onClick={logout}>
              Log out
            </button>
          </div>

          <div className="hero-banner">
            <div>
              <p className="banner-label">Signed in as</p>
              <h2>{getFullName(currentUser)}</h2>
              <p className="banner-text">{dashboard.summary}</p>
            </div>
            <span className={`role-pill ${currentUser.role}`}>{currentUser.role}</span>
          </div>
        </section>

        <section className="metrics-grid">
          {dashboard.metrics.map((metric) => (
            <article className="metric-card" key={metric.label}>
              <p className="metric-label">{metric.label}</p>
              <strong className="metric-value">{metric.value}</strong>
              <p className="metric-note">{metric.note}</p>
            </article>
          ))}
        </section>

        <section className="dashboard-grid">
          <article className="panel-card">
            <div className="panel-heading">
              <p className="eyebrow">Quick Actions</p>
              <h3>What would you like to do next?</h3>
            </div>
            <div className="action-list">
              {dashboard.actions.map((action) => (
                <article className="action-card" key={action.title}>
                  <strong>{action.title}</strong>
                  <p>{action.description}</p>
                </article>
              ))}
            </div>
          </article>

          <article className="panel-card compact">
            <div className="panel-heading">
              <p className="eyebrow">Profile</p>
              <h3>Account Summary</h3>
            </div>

            <dl className="profile-list">
              <div>
                <dt>Name</dt>
                <dd>{getFullName(currentUser)}</dd>
              </div>
              <div>
                <dt>Email</dt>
                <dd>{currentUser.email ?? 'Not provided'}</dd>
              </div>
              <div>
                <dt>Role</dt>
                <dd>{currentUser.role}</dd>
              </div>
              <div>
                <dt>Phone</dt>
                <dd>{currentUser.phoneNumber ?? 'Not provided'}</dd>
              </div>
            </dl>
          </article>
        </section>
      </main>
    )
  }

  return (
    <main className="login-shell">
      <section className="login-card">
        <div className="card-heading">
          <p className="eyebrow">Ticket Reservation Application</p>
          <h1>{mode === 'login' ? 'Login' : 'Register'}</h1>
          <p className="support-text">
            {mode === 'login'
              ? 'Sign in with your email or phone number.'
              : 'Create an account with your personal information. Email or phone number is required.'}
          </p>
        </div>

        <div className="auth-tabs" role="tablist" aria-label="Authentication screens">
          <button
            type="button"
            className={`auth-tab ${mode === 'login' ? 'active' : ''}`}
            onClick={() => {
              setMode('login')
              setError('')
            }}
          >
            Login
          </button>
          <button
            type="button"
            className={`auth-tab ${mode === 'register' ? 'active' : ''}`}
            onClick={() => {
              setMode('register')
              setError('')
            }}
          >
            Register
          </button>
        </div>

        {mode === 'login' ? (
          <form className="login-form" onSubmit={handleLoginSubmit}>
            <label className="field">
              <span>Email or phone number</span>
              <input
                type="text"
                name="identifier"
                value={loginIdentifier}
                onChange={(event) => setLoginIdentifier(event.target.value)}
                placeholder="Enter your email or phone number"
                autoComplete="username"
              />
            </label>

            <label className="field">
              <span>Password</span>
              <div className="password-row">
                <input
                  type={showPassword ? 'text' : 'password'}
                  name="password"
                  value={loginPassword}
                  onChange={(event) => setLoginPassword(event.target.value)}
                  placeholder="Enter your password"
                  autoComplete="current-password"
                />
                <button
                  type="button"
                  className="ghost-button"
                  onClick={() => setShowPassword((value) => !value)}
                >
                  {showPassword ? 'Hide' : 'Show'}
                </button>
              </div>
            </label>

            <button type="submit" className="primary-button" disabled={isSubmitting}>
              {isSubmitting ? 'Signing in...' : 'Log in'}
            </button>
          </form>
        ) : (
          <form className="login-form" onSubmit={handleRegisterSubmit}>
            <div className="split-fields">
              <label className="field">
                <span>First name</span>
                <input
                  type="text"
                  name="firstName"
                  value={registrationForm.firstName}
                  onChange={(event) =>
                    setRegistrationForm((current) => ({
                      ...current,
                      firstName: event.target.value,
                    }))
                  }
                  placeholder="First name"
                  autoComplete="given-name"
                />
              </label>

              <label className="field">
                <span>Last name</span>
                <input
                  type="text"
                  name="lastName"
                  value={registrationForm.lastName}
                  onChange={(event) =>
                    setRegistrationForm((current) => ({
                      ...current,
                      lastName: event.target.value,
                    }))
                  }
                  placeholder="Last name"
                  autoComplete="family-name"
                />
              </label>
            </div>

            <label className="field">
              <span>Email</span>
              <input
                type="email"
                name="email"
                value={registrationForm.email}
                onChange={(event) =>
                  setRegistrationForm((current) => ({
                    ...current,
                    email: event.target.value,
                  }))
                }
                placeholder="Enter your email"
                autoComplete="email"
              />
            </label>

            <label className="field">
              <span>Phone number</span>
              <input
                type="tel"
                name="phoneNumber"
                value={registrationForm.phoneNumber}
                onChange={(event) =>
                  setRegistrationForm((current) => ({
                    ...current,
                    phoneNumber: event.target.value,
                  }))
                }
                placeholder="Enter your phone number"
                autoComplete="tel"
              />
            </label>

            <label className="field">
              <span>Password</span>
              <div className="password-row">
                <input
                  type={showPassword ? 'text' : 'password'}
                  name="password"
                  value={registrationForm.password}
                  onChange={(event) =>
                    setRegistrationForm((current) => ({
                      ...current,
                      password: event.target.value,
                    }))
                  }
                  placeholder="At least 8 characters"
                  autoComplete="new-password"
                />
                <button
                  type="button"
                  className="ghost-button"
                  onClick={() => setShowPassword((value) => !value)}
                >
                  {showPassword ? 'Hide' : 'Show'}
                </button>
              </div>
            </label>

            <p className="helper-text">Provide at least one contact method: email or phone number.</p>

            <button type="submit" className="primary-button" disabled={isSubmitting}>
              {isSubmitting ? 'Creating account...' : 'Create account'}
            </button>
          </form>
        )}

        {error ? (
          <p className="message error-message" role="alert">
            {error}
          </p>
        ) : null}

        {successMessage ? <p className="message info-message">{successMessage}</p> : null}

        {!successMessage ? (
          <div className="message info-message">
            {mode === 'login'
              ? 'Use your registered email or phone number to sign in.'
              : 'New registrations are created as customer accounts.'}
          </div>
        ) : null}
      </section>
    </main>
  )
}

export default App
