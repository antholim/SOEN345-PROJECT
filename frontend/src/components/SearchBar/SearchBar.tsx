export function SearchBar() {
  return (
    <div className="header-search">
      <div className="search-bar">
        <div className="search-segment">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
            <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z" />
            <circle cx="12" cy="10" r="3" />
          </svg>
          <div className="search-segment-inner">
            <span className="search-label">LOCATION</span>
            <input type="text" className="search-input" placeholder="City or Postal Code" />
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
            <span className="search-label">DATES</span>
            <div className="dates-row">
              <span>All Dates</span>
              <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
                <polyline points="6 9 12 15 18 9" />
              </svg>
            </div>
          </div>
        </div>

        <div className="search-divider" />

        <div className="search-segment search-segment--grow">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
            <circle cx="11" cy="11" r="8" />
            <line x1="21" y1="21" x2="16.65" y2="16.65" />
          </svg>
          <div className="search-segment-inner">
            <span className="search-label">SEARCH</span>
            <input type="text" className="search-input" placeholder="Artist, Event or Venue" />
          </div>
        </div>

        <button type="button" className="search-button">Search</button>
      </div>
    </div>
  )
}
