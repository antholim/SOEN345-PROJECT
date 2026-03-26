import { useState } from 'react'
import { Header } from '../../components/Header/Header'
import { EventList } from '../../components/EventList/EventList'
import { MyEventsList } from '../../components/MyEventsList/MyEventsList'
import type { ActiveTab, EventFilter } from '../../types'

const EMPTY_FILTER: EventFilter = { city: '', category: '', fromDate: '' }

export function DashboardScreen() {
  const [activeTab, setActiveTab] = useState<ActiveTab>('events')
  const [filter, setFilter] = useState<EventFilter>(EMPTY_FILTER)
  const [appliedFilter, setAppliedFilter] = useState<EventFilter>(EMPTY_FILTER)

  function handleSearch() {
    setAppliedFilter({ ...filter })
  }

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
            <EventList filter={appliedFilter} />
          </div>
        )}
        {activeTab === 'my-events' && (
          <div className="tab-panel">
            <MyEventsList />
          </div>
        )}
      </main>
    </div>
  )
}
