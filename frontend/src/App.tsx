import { useState, useRef, useEffect } from 'react'
import './App.css'

type UserRole = 'CUSTOMER' | 'ADMIN'
type AuthMode = 'login' | 'register'
type ActiveTab = 'events' | 'my-events'

type UserRecord = {
  userId: number
  firstName: string
  lastName: string
  email: string | null
  phoneNumber: string | null
  role: UserRole
  createdAt: string
}

type ApiErrorResponse = {
  detail?: string
  message?: string
}

const API_BASE_URL = (import.meta.env.VITE_API_BASE_URL ?? '').replace(/\/$/, '')
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
  const [currentUser, setCurrentUser] = useState<UserRecord | null>(() => {
    try {
      const stored = localStorage.getItem('currentUser')
      return stored ? (JSON.parse(stored) as UserRecord) : null
    } catch {
      return null
    }
  })
  const [error, setError] = useState('')
  const [successMessage, setSuccessMessage] = useState('')
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [activeTab, setActiveTab] = useState<ActiveTab>('events')
  const [showProfile, setShowProfile] = useState(false)

  const handleLoginSubmit = async (event: { preventDefault(): void }) => {
    event.preventDefault()
    if (isSubmitting) return
    setError('')
    setSuccessMessage('')
    setIsSubmitting(true)
    try {
      const response = await fetch(getApiUrl('/api/auth/login'), {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email: loginIdentifier, password: loginPassword }),
      })
      if (!response.ok) {
        const errorResponse = (await response.json().catch(() => null)) as ApiErrorResponse | null
        setCurrentUser(null)
        setError(errorResponse?.detail ?? errorResponse?.message ?? 'We could not sign you in right now.')
        return
      }
      const user = (await response.json()) as UserRecord
      localStorage.setItem('currentUser', JSON.stringify(user))
      setCurrentUser(user)
      setError('')
    } catch {
      setCurrentUser(null)
      setError('The backend is unavailable. Start the Spring server and try again.')
    } finally {
      setIsSubmitting(false)
    }
  }

  const handleRegisterSubmit = async (event: { preventDefault(): void }) => {
    event.preventDefault()
    if (isSubmitting) return
    setError('')
    setSuccessMessage('')
    setIsSubmitting(true)
    try {
      const response = await fetch(getApiUrl('/api/auth/register'), {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(registrationForm),
      })
      if (!response.ok) {
        const errorResponse = (await response.json().catch(() => null)) as ApiErrorResponse | null
        setError(errorResponse?.detail ?? errorResponse?.message ?? 'We could not create your account right now.')
        return
      }
      const user = (await response.json()) as UserRecord
      localStorage.setItem('currentUser', JSON.stringify(user))
      setCurrentUser(user)
      setRegistrationForm({ firstName: '', lastName: '', email: '', phoneNumber: '', password: '' })
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
    localStorage.removeItem('currentUser')
    setCurrentUser(null)
    setLoginPassword('')
    setShowPassword(false)
    setError('')
    setActiveTab('events')
    setShowProfile(false)
  }

  const profileRef = useRef<HTMLDivElement>(null)

  useEffect(() => {
    const handleClickOutside = (e: MouseEvent) => {
      if (profileRef.current && !profileRef.current.contains(e.target as Node)) {
        setShowProfile(false)
      }
    }
    document.addEventListener('mousedown', handleClickOutside)
    return () => document.removeEventListener('mousedown', handleClickOutside)
  }, [])

  if (currentUser) {
    const initials =
      (currentUser.firstName[0] ?? '').toUpperCase() +
      (currentUser.lastName[0] ?? '').toUpperCase()

    const joinDate = new Date(currentUser.createdAt).toLocaleDateString('en-CA', {
      year: 'numeric', month: 'long', day: 'numeric',
    })

    return (
      <div className="app-shell">
        <header className="site-header">
          <div className="header-inner">
            <span className="tm-logo">TicketMonster</span>

            <nav className="header-nav" aria-label="Main navigation">
              <button
                type="button"
                className={`nav-link${activeTab === 'events' ? ' active' : ''}`}
                onClick={() => setActiveTab('events')}
              >
                Events
              </button>
              <button
                type="button"
                className={`nav-link${activeTab === 'my-events' ? ' active' : ''}`}
                onClick={() => setActiveTab('my-events')}
              >
                My Events
              </button>
            </nav>

            <div className="header-profile" ref={profileRef}>
              <button
                type="button"
                className="profile-toggle"
                onClick={() => setShowProfile((v) => !v)}
                aria-expanded={showProfile}
                aria-haspopup="true"
              >
                <div className="profile-avatar">{initials}</div>
                <span className="profile-name">
                  {currentUser.firstName} {currentUser.lastName}
                </span>
                <svg className={`profile-chevron${showProfile ? ' open' : ''}`} width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
                  <polyline points="6 9 12 15 18 9" />
                </svg>
              </button>

              {showProfile && (
                <div className="profile-dropdown">
                  <div className="profile-dropdown-header">
                    <div className="profile-dropdown-avatar">{initials}</div>
                    <div>
                      <p className="profile-dropdown-name">{currentUser.firstName} {currentUser.lastName}</p>
                      <span className={`profile-dropdown-role ${currentUser.role}`}>{currentUser.role}</span>
                    </div>
                  </div>

                  <dl className="profile-dropdown-details">
                    <div>
                      <dt>Email</dt>
                      <dd>{currentUser.email ?? '—'}</dd>
                    </div>
                    <div>
                      <dt>Phone</dt>
                      <dd>{currentUser.phoneNumber ?? '—'}</dd>
                    </div>
                    <div>
                      <dt>Member since</dt>
                      <dd>{joinDate}</dd>
                    </div>
                  </dl>

                  <button type="button" className="profile-dropdown-signout" onClick={logout}>
                    Sign out
                  </button>
                </div>
              )}
            </div>
          </div>

          {activeTab === 'events' && (
            <div className="header-search">
              <div className="search-bar">
                <div className="search-segment">
                  <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
                    <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z" />
                    <circle cx="12" cy="10" r="3" />
                  </svg>
                  <div className="search-segment-inner">
                    <span className="search-label">LOCATION</span>
                    <input type="text" className="search-input" placeholder="City or Postal Code" />
                  </div>
                </div>

                <div className="search-divider" />

                <div className="search-segment">
                  <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
                    <rect x="3" y="4" width="18" height="18" rx="2" ry="2" />
                    <line x1="16" y1="2" x2="16" y2="6" />
                    <line x1="8" y1="2" x2="8" y2="6" />
                    <line x1="3" y1="10" x2="21" y2="10" />
                  </svg>
                  <div className="search-segment-inner">
                    <span className="search-label">DATES</span>
                    <div className="dates-row">
                      <span>All Dates</span>
                      <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
                        <polyline points="6 9 12 15 18 9" />
                      </svg>
                    </div>
                  </div>
                </div>

                <div className="search-divider" />

                <div className="search-segment search-segment--grow">
                  <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
                    <circle cx="11" cy="11" r="8" />
                    <line x1="21" y1="21" x2="16.65" y2="16.65" />
                  </svg>
                  <div className="search-segment-inner">
                    <span className="search-label">SEARCH</span>
                    <input type="text" className="search-input" placeholder="Artist, Event or Venue" />
                  </div>
                </div>

                <button type="button" className="search-button">Search</button>
              </div>
            </div>
          )}
        </header>

        <main className="page-content">
          {activeTab === 'events' && (
            <div className="tab-panel" />
          )}
          {activeTab === 'my-events' && (
            <div className="tab-panel" />
          )}
        </main>
      </div>
    )
  }

  return (
    <main className="login-shell">
      <section className="login-card">
        <div className="card-heading">
          <p className="tm-logo-login">TicketMonster</p>
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
            onClick={() => { setMode('login'); setError('') }}
          >
            Login
          </button>
          <button
            type="button"
            className={`auth-tab ${mode === 'register' ? 'active' : ''}`}
            onClick={() => { setMode('register'); setError('') }}
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
                onChange={(e) => setLoginIdentifier(e.target.value)}
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
                  onChange={(e) => setLoginPassword(e.target.value)}
                  placeholder="Enter your password"
                  autoComplete="current-password"
                />
                <button type="button" className="ghost-button" onClick={() => setShowPassword((v) => !v)}>
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
                  onChange={(e) => setRegistrationForm((c) => ({ ...c, firstName: e.target.value }))}
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
                  onChange={(e) => setRegistrationForm((c) => ({ ...c, lastName: e.target.value }))}
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
                onChange={(e) => setRegistrationForm((c) => ({ ...c, email: e.target.value }))}
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
                onChange={(e) => setRegistrationForm((c) => ({ ...c, phoneNumber: e.target.value }))}
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
                  onChange={(e) => setRegistrationForm((c) => ({ ...c, password: e.target.value }))}
                  placeholder="At least 8 characters"
                  autoComplete="new-password"
                />
                <button type="button" className="ghost-button" onClick={() => setShowPassword((v) => !v)}>
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

        {error ? <p className="message error-message" role="alert">{error}</p> : null}
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
