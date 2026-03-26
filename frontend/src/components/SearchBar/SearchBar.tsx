import type { EventFilter } from '../../types'

const CATEGORIES = ['Concert', 'Sports', 'Arts & Theatre', 'Comedy', 'Family', 'Travel']

type SearchBarProps = {
  filter: EventFilter
  onChange: (filter: EventFilter) => void
  onSearch: () => void
}

export function SearchBar({ filter, onChange, onSearch }: SearchBarProps) {
  return (
    <div className="header-search">
      <div className="search-bar">

        <div className="search-segment search-segment--grow">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
            <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z" />
            <circle cx="12" cy="10" r="3" />
          </svg>
          <div className="search-segment-inner">
            <span className="search-label">LOCATION</span>
            <input
              type="text"
              className="search-input"
              placeholder="City"
              value={filter.city}
              onChange={e => onChange({ ...filter, city: e.target.value })}
            />
          </div>
        </div>

        <div className="search-divider" />

        <div className="search-segment">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
            <rect x="3" y="4" width="18" height="18" rx="2" ry="2" />
            <line x1="16" y1="2" x2="16" y2="6" />
            <line x1="8" y1="2" x2="8" y2="6" />
            <line x1="3" y1="10" x2="21" y2="10" />
          </svg>
          <div className="search-segment-inner">
            <span className="search-label">FROM DATE</span>
            <input
              type="date"
              className="search-input search-input--date"
              value={filter.fromDate}
              onChange={e => onChange({ ...filter, fromDate: e.target.value })}
            />
          </div>
        </div>

        <div className="search-divider" />

        <div className="search-segment">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
            <path d="M4 6h16M7 12h10M10 18h4" strokeLinecap="round" />
          </svg>
          <div className="search-segment-inner">
            <span className="search-label">CATEGORY</span>
            <select
              className="search-input search-input--select"
              value={filter.category}
              onChange={e => onChange({ ...filter, category: e.target.value })}
            >
              <option value="">All Categories</option>
              {CATEGORIES.map(c => (
                <option key={c} value={c}>{c}</option>
              ))}
            </select>
          </div>
        </div>

        <button type="button" className="search-button" onClick={onSearch}>Search</button>
      </div>
    </div>
  )
}
