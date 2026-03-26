import { getApiUrl } from '../utils/api'
import type { ApiErrorResponse, Event } from '../types'

export type CreateEventForm = {
  title: string
  description: string
  eventDate: string
  availableTickets: number
  price: number
  venueName: string
  venueCity: string
  venueAddress: string
  venueCapacity: number
  categoryName: string
  createdBy: number
}

export async function updateAdminEvent(eventId: number, form: CreateEventForm): Promise<Event> {
  const response = await fetch(getApiUrl(`/api/admin/events/${eventId}`), {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(form),
  })
  if (!response.ok) {
    const err = (await response.json().catch(() => ({}))) as ApiErrorResponse
    throw new Error(err.detail ?? err.message ?? `Error ${response.status}`)
  }
  return response.json() as Promise<Event>
}

export async function cancelAdminEvent(eventId: number): Promise<void> {
  const response = await fetch(getApiUrl(`/api/admin/events/${eventId}/cancel`), {
    method: 'PATCH',
  })
  if (!response.ok) {
    const err = (await response.json().catch(() => ({}))) as ApiErrorResponse
    throw new Error(err.detail ?? err.message ?? `Error ${response.status}`)
  }
}

export async function createEvent(form: CreateEventForm): Promise<Event> {
  const response = await fetch(getApiUrl('/api/admin/events'), {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(form),
  })

  if (!response.ok) {
    const err = (await response.json().catch(() => ({}))) as ApiErrorResponse
    throw new Error(err.detail ?? err.message ?? `Error ${response.status}`)
  }

  return response.json() as Promise<Event>
}
