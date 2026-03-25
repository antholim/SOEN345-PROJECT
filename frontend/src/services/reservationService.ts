import { getApiUrl } from '../utils/api'

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
