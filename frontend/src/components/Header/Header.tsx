import { useState, useRef } from 'react'
import { useUser } from '../../contexts/UserContext'
import { useClickOutside } from '../../hooks/useClickOutside'
import { SearchBar } from '../SearchBar/SearchBar'
import { ProfileDropdown } from '../ProfileDropdown/ProfileDropdown'
import type { ActiveTab } from '../../types'

type HeaderProps = {
  activeTab: ActiveTab
  onTabChange: (tab: ActiveTab) => void
}

export function Header({ activeTab, onTabChange }: HeaderProps) {
  const { currentUser } = useUser()
  const [showProfile, setShowProfile] = useState(false)
  const profileRef = useRef<HTMLDivElement>(null)

  useClickOutside(profileRef, () => setShowProfile(false))

  if (!currentUser) return null

  const initials =
    (currentUser.firstName[0] ?? '').toUpperCase() +
    (currentUser.lastName[0] ?? '').toUpperCase()

  return (
    <header className="site-header">
      <div className="header-inner">
        <span className="tm-logo">TicketMonster</span>

        <nav className="header-nav" aria-label="Main navigation">
          <button
            type="button"
            className={`nav-link${activeTab === 'events' ? ' active' : ''}`}
            onClick={() => onTabChange('events')}
          >
            Events
          </button>
          <button
            type="button"
            className={`nav-link${activeTab === 'my-events' ? ' active' : ''}`}
            onClick={() => onTabChange('my-events')}
          >
            My Events
          </button>
        </nav>

        <div className="header-profile" ref={profileRef}>
          <button
            type="button"
            className="profile-toggle"
            onClick={() => setShowProfile((v) => !v)}
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

      {activeTab === 'events' && <SearchBar />}
    </header>
  )
}
