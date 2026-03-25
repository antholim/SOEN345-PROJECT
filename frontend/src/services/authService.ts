import { getApiUrl } from '../utils/api'
import type { UserRecord } from '../models/user'
import type { ApiErrorResponse } from '../types'

export type RegisterForm = {
  firstName: string
  lastName: string
  email: string
  phoneNumber: string
  password: string
}

export async function login(identifier: string, password: string): Promise<UserRecord> {
  const response = await fetch(getApiUrl('/api/auth/login'), {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email: identifier, password }),
  })

  if (!response.ok) {
    const err = (await response.json().catch(() => null)) as ApiErrorResponse | null
    throw new Error(err?.detail ?? err?.message ?? 'We could not sign you in right now.')
  }

  return response.json() as Promise<UserRecord>
}

export async function register(form: RegisterForm): Promise<UserRecord> {
  const response = await fetch(getApiUrl('/api/auth/register'), {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(form),
  })

  if (!response.ok) {
    const err = (await response.json().catch(() => null)) as ApiErrorResponse | null
    throw new Error(err?.detail ?? err?.message ?? 'We could not create your account right now.')
  }

  return response.json() as Promise<UserRecord>
}
