import { useState } from 'react'
import { useUser } from '../../contexts/UserContext'
import { Header } from '../../components/Header/Header'
import { EventList } from '../../components/EventList/EventList'
import { MyEventsList } from '../../components/MyEventsList/MyEventsList'
import { AddEventModal } from '../../components/AddEventModal/AddEventModal'
import type { ActiveTab, EventFilter } from '../../types'

const EMPTY_FILTER: EventFilter = { city: '', category: '', fromDate: '' }

export function DashboardScreen() {
  const { currentUser } = useUser()
  const [activeTab, setActiveTab] = useState<ActiveTab>('events')
  const [filter, setFilter] = useState<EventFilter>(EMPTY_FILTER)
  const [appliedFilter, setAppliedFilter] = useState<EventFilter>(EMPTY_FILTER)
  const [showAddModal, setShowAddModal] = useState(false)

  function handleSearch() {
    setAppliedFilter({ ...filter })
  }

  const isAdmin = currentUser?.role === 'ADMIN'

  return (
    <div className="app-shell">
      <Header
        activeTab={activeTab}
        onTabChange={setActiveTab}
        filter={filter}
        onFilterChange={setFilter}
        onSearch={handleSearch}
      />

      <main className="page-content">
        {activeTab === 'events' && (
          <div className="tab-panel">
            {isAdmin && (
              <div className="admin-toolbar">
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
            )}

            <EventList filter={appliedFilter} isAdmin={isAdmin} />
          </div>
        )}
        {activeTab === 'my-events' && (
          <div className="tab-panel">
            <MyEventsList />
          </div>
        )}
      </main>

      {showAddModal && (
        <AddEventModal
          onClose={() => setShowAddModal(false)}
          onSaved={() => {
            setShowAddModal(false)
            setAppliedFilter(f => ({ ...f }))
          }}
        />
      )}
    </div>
  )
}
