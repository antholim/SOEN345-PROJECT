import { useState } from 'react'
import { Header } from '../../components/Header/Header'
import { EventList } from '../../components/EventList/EventList'
import type { ActiveTab } from '../../types'

export function DashboardScreen() {
  const [activeTab, setActiveTab] = useState<ActiveTab>('events')

  return (
    <div className="app-shell">
      <Header activeTab={activeTab} onTabChange={setActiveTab} />

      <main className="page-content">
        {activeTab === 'events' && (
          <div className="tab-panel">
            <EventList />
          </div>
        )}
        {activeTab === 'my-events' && <div className="tab-panel" />}
      </main>
    </div>
  )
}
