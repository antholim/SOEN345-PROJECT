import { useEffect, useState } from 'react'
import { getActiveEvents } from '../../services/eventService'
import type { Event } from '../../types'

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

export function EventList() {
  const [events, setEvents] = useState<Event[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    getActiveEvents()
      .then(setEvents)
      .catch((err: Error) => setError(err.message))
      .finally(() => setLoading(false))
  }, [])

  if (loading) {
    return (
      <div className="event-list-state">
        <div className="event-list-spinner" />
        <p>Loading events…</p>
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

  if (events.length === 0) {
    return (
      <div className="event-list-state">
        <p>No upcoming events found.</p>
      </div>
    )
  }

  return (
    <div className="event-list">
      {events.map((event) => {
        const date = new Date(event.eventDate)
        const month = MONTH_ABBR[date.getMonth()]
        const day = date.getDate()

        return (
          <div key={event.eventId} className="event-row">
            <div className="event-date-box">
              <span className="event-date-month">{month}</span>
              <span className="event-date-day">{day}</span>
            </div>

            <div className="event-info">
              <p className="event-time">
                {formatDay(event.eventDate)} • {formatTime(event.eventDate)}
              </p>
              <h3 className="event-title">{event.title}</h3>
              <p className="event-venue">
                {event.venueCity} • {event.venueName}
              </p>
              <span className="event-category">{event.categoryName}</span>
            </div>

            <div className="event-actions">
              <p className="event-price">${event.price.toFixed(2)}</p>
              <p className="event-tickets">{event.availableTickets.toLocaleString()} tickets left</p>
            </div>
          </div>
        )
      })}
    </div>
  )
}
