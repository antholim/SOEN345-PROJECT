import { useEffect, useRef, useState } from 'react'
import { useUser } from '../../contexts/UserContext'
import { getCategories } from '../../services/categoryService'
import { createEvent, updateAdminEvent } from '../../services/adminEventService'
import type { CreateEventForm } from '../../services/adminEventService'
import type { Event } from '../../types'

const BASE_FORM: Omit<CreateEventForm, 'createdBy'> = {
  title: '',
  description: '',
  eventDate: '',
  availableTickets: 100,
  price: 0,
  venueName: '',
  venueCity: '',
  venueAddress: '',
  venueCapacity: 1000,
  categoryName: '',
}

function eventToForm(event: Event): Omit<CreateEventForm, 'createdBy'> {
  return {
    title: event.title,
    description: event.description,
    eventDate: event.eventDate.slice(0, 16), // "2026-04-01T20:44" for datetime-local
    availableTickets: event.availableTickets,
    price: event.price,
    venueName: event.venueName,
    venueCity: event.venueCity,
    venueAddress: event.venueAddress,
    venueCapacity: event.venueCapacity,
    categoryName: event.categoryName,
  }
}

type Props = {
  event?: Event
  onClose: () => void
  onSaved?: (event: Event) => void
}

export function AddEventModal({ event: editEvent, onClose, onSaved }: Props) {
  const { currentUser } = useUser()
  const isEdit = editEvent !== undefined
  const [form, setForm] = useState<Omit<CreateEventForm, 'createdBy'>>(
    isEdit ? eventToForm(editEvent) : BASE_FORM
  )
  const [categories, setCategories] = useState<string[]>([])
  const [submitting, setSubmitting] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const backdropRef = useRef<HTMLDivElement>(null)

  useEffect(() => {
    getCategories().then(setCategories)
  }, [])

  function set<K extends keyof typeof form>(key: K, value: (typeof form)[K]) {
    setForm(prev => ({ ...prev, [key]: value }))
  }

  function handleBackdropClick(e: React.MouseEvent) {
    if (e.target === backdropRef.current) onClose()
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    setSubmitting(true)
    setError(null)
    try {
      let saved: Event
      if (isEdit) {
        saved = await updateAdminEvent(editEvent.eventId, { ...form, createdBy: currentUser!.userId })
      } else {
        saved = await createEvent({ ...form, createdBy: currentUser!.userId })
      }
      onSaved?.(saved)
      onClose()
    } catch (err) {
      setError((err as Error).message)
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="modal-backdrop" ref={backdropRef} onClick={handleBackdropClick}>
      <div className="modal" role="dialog" aria-modal="true" aria-labelledby="modal-title">
        <div className="modal-header">
          <h2 id="modal-title" className="modal-title">{isEdit ? 'Edit Event' : 'Add Event'}</h2>
          <button type="button" className="modal-close" onClick={onClose} aria-label="Close">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round">
              <line x1="18" y1="6" x2="6" y2="18" />
              <line x1="6" y1="6" x2="18" y2="18" />
            </svg>
          </button>
        </div>

        <form className="modal-form" onSubmit={handleSubmit}>
          <div className="modal-section-label">Event details</div>

          <div className="modal-field">
            <label className="modal-label" htmlFor="title">Title</label>
            <input id="title" className="modal-input" type="text" required value={form.title} onChange={e => set('title', e.target.value)} />
          </div>

          <div className="modal-field">
            <label className="modal-label" htmlFor="description">Description</label>
            <textarea id="description" className="modal-input modal-textarea" rows={3} value={form.description} onChange={e => set('description', e.target.value)} />
          </div>

          <div className="modal-row">
            <div className="modal-field">
              <label className="modal-label" htmlFor="eventDate">Date & Time</label>
              <input id="eventDate" className="modal-input" type="datetime-local" required value={form.eventDate} onChange={e => set('eventDate', e.target.value)} />
            </div>
            <div className="modal-field">
              <label className="modal-label" htmlFor="categoryName">Category</label>
              <select id="categoryName" className="modal-input modal-select" required value={form.categoryName} onChange={e => set('categoryName', e.target.value)}>
                <option value="">Select category</option>
                {categories.map(c => <option key={c} value={c}>{c}</option>)}
              </select>
            </div>
          </div>

          <div className="modal-row">
            <div className="modal-field">
              <label className="modal-label" htmlFor="availableTickets">Available Tickets</label>
              <input id="availableTickets" className="modal-input" type="number" min={1} required value={form.availableTickets} onChange={e => set('availableTickets', Number(e.target.value))} />
            </div>
            <div className="modal-field">
              <label className="modal-label" htmlFor="price">Price ($)</label>
              <input id="price" className="modal-input" type="number" min={0} step={0.01} required value={form.price} onChange={e => set('price', Number(e.target.value))} />
            </div>
          </div>

          <div className="modal-section-label">Venue</div>

          <div className="modal-row">
            <div className="modal-field">
              <label className="modal-label" htmlFor="venueName">Venue Name</label>
              <input id="venueName" className="modal-input" type="text" required value={form.venueName} onChange={e => set('venueName', e.target.value)} />
            </div>
            <div className="modal-field">
              <label className="modal-label" htmlFor="venueCity">City</label>
              <input id="venueCity" className="modal-input" type="text" required value={form.venueCity} onChange={e => set('venueCity', e.target.value)} />
            </div>
          </div>

          <div className="modal-row">
            <div className="modal-field modal-field--grow">
              <label className="modal-label" htmlFor="venueAddress">Address</label>
              <input id="venueAddress" className="modal-input" type="text" required value={form.venueAddress} onChange={e => set('venueAddress', e.target.value)} />
            </div>
            <div className="modal-field">
              <label className="modal-label" htmlFor="venueCapacity">Capacity</label>
              <input id="venueCapacity" className="modal-input" type="number" min={1} required value={form.venueCapacity} onChange={e => set('venueCapacity', Number(e.target.value))} />
            </div>
          </div>

          {error && <p className="modal-error">{error}</p>}

          <div className="modal-footer">
            <button type="button" className="modal-btn modal-btn--secondary" onClick={onClose}>Cancel</button>
            <button type="submit" className="modal-btn modal-btn--primary" disabled={submitting}>
              {submitting ? (isEdit ? 'Saving…' : 'Creating…') : (isEdit ? 'Save Changes' : 'Create Event')}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}
