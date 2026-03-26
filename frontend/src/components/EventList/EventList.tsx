import { useEffect, useState } from 'react'
import { getActiveEvents } from '../../services/eventService'
import { createReservation } from '../../services/reservationService'
import { useUser } from '../../contexts/UserContext'
import type { Event, EventFilter } from '../../types'

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

export function EventList({ filter }: { filter: EventFilter }) {
  const { currentUser } = useUser()
  const [events, setEvents] = useState<Event[]>([])
  const [loadedFilter, setLoadedFilter] = useState<EventFilter | null>(null)
  const [loadingMore, setLoadingMore] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [page, setPage] = useState(0)
  const [hasMore, setHasMore] = useState(false)
  const [quantities, setQuantities] = useState<Record<number, number>>({})
  const [reserving, setReserving] = useState<Record<number, boolean>>({})
  const [reserved, setReserved] = useState<Record<number, boolean>>({})

  const loading = loadedFilter !== filter

  useEffect(() => {
    getActiveEvents(0, filter)
      .then(({ events, hasMore }) => {
        setEvents(events)
        setHasMore(hasMore)
        setPage(0)
        setError(null)
        setLoadedFilter(filter)
      })
      .catch((err: Error) => {
        setError(err.message)
        setLoadedFilter(filter)
      })
  }, [filter])

  function reserve(eventId: number) {
    if (!currentUser) return
    const qty = quantities[eventId] ?? 1
    setReserving(prev => ({ ...prev, [eventId]: true }))
    createReservation(currentUser.userId, eventId, qty)
      .then(() => {
        setReserved(prev => ({ ...prev, [eventId]: true }))
        setEvents(prev => prev.map(e =>
          e.eventId === eventId
            ? { ...e, availableTickets: e.availableTickets - qty }
            : e
        ))
      })
      .catch((err: Error) => setError(err.message))
      .finally(() => setReserving(prev => ({ ...prev, [eventId]: false })))
  }

  function loadMore() {
    const nextPage = page + 1
    setLoadingMore(true)
    getActiveEvents(nextPage, filter)
      .then(({ events: newEvents, hasMore }) => {
        setEvents(prev => [...prev, ...newEvents])
        setHasMore(hasMore)
        setPage(nextPage)
      })
      .catch((err: Error) => setError(err.message))
      .finally(() => setLoadingMore(false))
  }

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
    <>
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
                <div className="event-price-col">
                  <p className="event-price">${event.price.toFixed(2)}</p>
                  <p className="event-tickets">{event.availableTickets.toLocaleString()} tickets left</p>
                </div>
                <div className="event-reserve-col">
                  <input
                    className="event-qty-input"
                    type="number"
                    min={1}
                    max={event.availableTickets}
                    value={quantities[event.eventId] ?? 1}
                    onChange={e => setQuantities(prev => ({ ...prev, [event.eventId]: Math.max(1, Number(e.target.value)) }))}
                    disabled={reserved[event.eventId]}
                  />
                  <button
                    className={`event-reserve-btn${reserved[event.eventId] ? ' event-reserve-btn--done' : ''}`}
                    onClick={() => reserve(event.eventId)}
                    disabled={reserving[event.eventId] || reserved[event.eventId]}
                  >
                    {reserved[event.eventId] ? 'Reserved!' : reserving[event.eventId] ? '…' : 'Reserve'}
                  </button>
                </div>
              </div>
            </div>
          )
        })}
      </div>

      {hasMore && (
        <div className="load-more-wrap">
          <button className="load-more-btn" onClick={loadMore} disabled={loadingMore}>
            {loadingMore ? 'Loading…' : 'Load more'}
          </button>
        </div>
      )}
    </>
  )
}
