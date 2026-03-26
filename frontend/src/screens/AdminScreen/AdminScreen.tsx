import { useState, useRef } from 'react'
import { useUser } from '../../contexts/UserContext'
import { useClickOutside } from '../../hooks/useClickOutside'
import { ProfileDropdown } from '../../components/ProfileDropdown/ProfileDropdown'
import { AddEventModal } from '../../components/AddEventModal/AddEventModal'
import type { Event } from '../../types'

export function AdminScreen() {
  const { currentUser } = useUser()
  const [showProfile, setShowProfile] = useState(false)
  const [showAddModal, setShowAddModal] = useState(false)
  const [recentlyAdded, setRecentlyAdded] = useState<Event[]>([])
  const profileRef = useRef<HTMLDivElement>(null)

  useClickOutside(profileRef, () => setShowProfile(false))

  if (!currentUser) return null

  const initials =
    (currentUser.firstName[0] ?? '').toUpperCase() +
    (currentUser.lastName[0] ?? '').toUpperCase()

  function handleEventCreated(event: Event) {
    setRecentlyAdded(prev => [event, ...prev])
  }

  return (
    <div className="app-shell">
      <header className="site-header">
        <div className="header-inner">
          <span className="tm-logo">TicketMonster</span>

          <div className="admin-badge">Admin Panel</div>

          <div className="header-profile" ref={profileRef}>
            <button
              type="button"
              className="profile-toggle"
              onClick={() => setShowProfile(v => !v)}
              aria-expanded={showProfile}
              aria-haspopup="true"
            >
              <div className="profile-avatar">{initials}</div>
              <span className="profile-name">
                {currentUser.firstName} {currentUser.lastName}
              </span>
              <svg
                className={`profile-chevron${showProfile ? ' open' : ''}`}
                width="14" height="14" viewBox="0 0 24 24"
                fill="none" stroke="currentColor" strokeWidth="2.5"
                strokeLinecap="round" strokeLinejoin="round"
                aria-hidden="true"
              >
                <polyline points="6 9 12 15 18 9" />
              </svg>
            </button>

            {showProfile && <ProfileDropdown onClose={() => setShowProfile(false)} />}
          </div>
        </div>
      </header>

      <main className="page-content">
        <div className="admin-toolbar">
          <h1 className="admin-page-title">Events</h1>
          <button
            type="button"
            className="admin-add-btn"
            onClick={() => setShowAddModal(true)}
          >
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" aria-hidden="true">
              <line x1="12" y1="5" x2="12" y2="19" />
              <line x1="5" y1="12" x2="19" y2="12" />
            </svg>
            Add Event
          </button>
        </div>

        {recentlyAdded.length > 0 && (
          <div className="admin-recently-added">
            <p className="admin-recently-label">Recently added ({recentlyAdded.length})</p>
            <div className="event-list">
              {recentlyAdded.map(event => {
                const date = new Date(event.eventDate)
                const months = ['JAN','FEB','MAR','APR','MAY','JUN','JUL','AUG','SEP','OCT','NOV','DEC']
                return (
                  <div key={event.eventId} className="event-row">
                    <div className="event-date-box">
                      <span className="event-date-month">{months[date.getMonth()]}</span>
                      <span className="event-date-day">{date.getDate()}</span>
                    </div>
                    <div className="event-info">
                      <h3 className="event-title">{event.title}</h3>
                      <p className="event-venue">{event.venueCity} • {event.venueName}</p>
                      <span className="event-category">{event.categoryName}</span>
                    </div>
                    <div className="event-actions">
                      <div className="event-price-col">
                        <p className="event-price">${event.price.toFixed(2)}</p>
                        <p className="event-tickets">{event.availableTickets.toLocaleString()} tickets</p>
                      </div>
                    </div>
                  </div>
                )
              })}
            </div>
          </div>
        )}

        {recentlyAdded.length === 0 && (
          <div className="event-list-state">
            <p>No events yet. Click "Add Event" to create one.</p>
          </div>
        )}
      </main>

      {showAddModal && (
        <AddEventModal
          onClose={() => setShowAddModal(false)}
          onSaved={handleEventCreated}
        />
      )}
    </div>
  )
}
