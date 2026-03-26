import { useEffect, useState } from 'react'
import { getUserReservations, cancelReservation } from '../../services/reservationService'
import { useUser } from '../../contexts/UserContext'
import type { Reservation } from '../../types'

const MONTH_ABBR = ['JAN', 'FEB', 'MAR', 'APR', 'MAY', 'JUN', 'JUL', 'AUG', 'SEP', 'OCT', 'NOV', 'DEC']

function formatTime(dateStr: string): string {
  const d = new Date(dateStr)
  const h = d.getHours()
  const m = d.getMinutes()
  const period = h >= 12 ? 'PM' : 'AM'
  const hour = h % 12 || 12
  return `${hour}:${m.toString().padStart(2, '0')} ${period}`
}

function formatDay(dateStr: string): string {
  const days = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat']
  return days[new Date(dateStr).getDay()]
}

export function MyEventsList() {
  const { currentUser } = useUser()
  const [reservations, setReservations] = useState<Reservation[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [cancelling, setCancelling] = useState<Record<number, boolean>>({})

  useEffect(() => {
    if (!currentUser) return
    getUserReservations(currentUser.userId)
      .then(setReservations)
      .catch((err: Error) => setError(err.message))
      .finally(() => setLoading(false))
  }, [currentUser])

  function handleCancel(reservationId: number) {
    setCancelling(prev => ({ ...prev, [reservationId]: true }))
    cancelReservation(reservationId)
      .then(() => setReservations(prev =>
        prev.map(r => r.reservationId === reservationId ? { ...r, status: 'CANCELLED' } : r)
      ))
      .catch((err: Error) => setError(err.message))
      .finally(() => setCancelling(prev => ({ ...prev, [reservationId]: false })))
  }

  if (loading) {
    return (
      <div className="event-list-state">
        <div className="event-list-spinner" />
        <p>Loading your reservations…</p>
      </div>
    )
  }

  if (error) {
    return (
      <div className="event-list-state event-list-state--error">
        <p>{error}</p>
      </div>
    )
  }

  if (reservations.length === 0) {
    return (
      <div className="event-list-state">
        <p>You have no reservations yet.</p>
      </div>
    )
  }

  return (
    <div className="event-list">
      {reservations.map((r) => {
        const date = new Date(r.eventDate)
        const month = MONTH_ABBR[date.getMonth()]
        const day = date.getDate()

        return (
          <div key={r.reservationId} className="event-row">
            <div className="event-date-box">
              <span className="event-date-month">{month}</span>
              <span className="event-date-day">{day}</span>
            </div>

            <div className="event-info">
              <p className="event-time">
                {formatDay(r.eventDate)} • {formatTime(r.eventDate)}
              </p>
              <h3 className="event-title">{r.eventTitle}</h3>
              <p className="event-venue">
                {r.venueCity} • {r.venueName}
              </p>
              <span className="event-category">{r.categoryName}</span>
            </div>

            <div className="event-actions">
              <div className="event-price-col">
                <p className="event-price">${r.totalPrice.toFixed(2)}</p>
                <p className="event-tickets">
                  {r.numberOfTickets} ticket{r.numberOfTickets !== 1 ? 's' : ''}
                </p>
              </div>
              {r.status === 'CANCELLED' && (
                <span className="my-events-status my-events-status--cancelled">CANCELLED</span>
              )}
              {r.status !== 'CANCELLED' && (
                <button
                  className="reservation-delete-btn"
                  onClick={() => handleCancel(r.reservationId)}
                  disabled={cancelling[r.reservationId]}
                >
                  {cancelling[r.reservationId] ? '…' : 'Cancel'}
                </button>
              )}
            </div>
          </div>
        )
      })}
    </div>
  )
}
