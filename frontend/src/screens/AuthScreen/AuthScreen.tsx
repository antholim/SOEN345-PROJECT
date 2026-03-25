import { useState } from 'react'
import { useUser } from '../../contexts/UserContext'
import { login, register } from '../../services/authService'
import type { AuthMode } from '../../types'
import type { RegisterForm } from '../../services/authService'

export function AuthScreen() {
  const { saveUser } = useUser()

  const [mode, setMode] = useState<AuthMode>('login')
  const [loginIdentifier, setLoginIdentifier] = useState('')
  const [loginPassword, setLoginPassword] = useState('')
  const [registrationForm, setRegistrationForm] = useState<RegisterForm>({
    firstName: '',
    lastName: '',
    email: '',
    phoneNumber: '',
    password: '',
  })
  const [showPassword, setShowPassword] = useState(false)
  const [error, setError] = useState('')
  const [successMessage, setSuccessMessage] = useState('')
  const [isSubmitting, setIsSubmitting] = useState(false)

  const handleLoginSubmit = async (event: { preventDefault(): void }) => {
    event.preventDefault()
    if (isSubmitting) return
    setError('')
    setSuccessMessage('')
    setIsSubmitting(true)
    try {
      const user = await login(loginIdentifier, loginPassword)
      saveUser(user)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'We could not sign you in right now.')
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
      const user = await register(registrationForm)
      saveUser(user)
      setRegistrationForm({ firstName: '', lastName: '', email: '', phoneNumber: '', password: '' })
      setLoginIdentifier(user.email ?? user.phoneNumber ?? '')
      setLoginPassword('')
      setSuccessMessage('Account created successfully.')
    } catch (err) {
      setError(err instanceof Error ? err.message : 'We could not create your account right now.')
    } finally {
      setIsSubmitting(false)
    }
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
