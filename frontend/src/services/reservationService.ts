import { getApiUrl } from '../utils/api'
import type { Reservation } from '../types'

type ReservationResponse = {
  reservationId: number
  eventTitle: string
  numberOfTickets: number
  totalPrice: number
  status: string
}

export async function createReservation(
  userId: number,
  eventId: number,
  numberOfTickets: number
): Promise<ReservationResponse> {
  const response = await fetch(getApiUrl('/api/reservations'), {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ userId, eventId, numberOfTickets }),
  })
  if (!response.ok) {
    const body = await response.text().catch(() => '')
    throw new Error(`Reservation failed (${response.status}): ${body}`)
  }
  return response.json() as Promise<ReservationResponse>
}

export async function getUserReservations(userId: number): Promise<Reservation[]> {
  const response = await fetch(getApiUrl(`/api/reservations?userId=${userId}`))
  if (!response.ok) {
    const body = await response.text().catch(() => '')
    throw new Error(`Failed to load reservations (${response.status}): ${body}`)
  }
  return response.json() as Promise<Reservation[]>
}

export async function cancelReservation(reservationId: number): Promise<void> {
  const response = await fetch(getApiUrl(`/api/reservations/${reservationId}`), {
    method: 'DELETE',
  })
  if (!response.ok) {
    const body = await response.text().catch(() => '')
    throw new Error(`Cancel failed (${response.status}): ${body}`)
  }
}
