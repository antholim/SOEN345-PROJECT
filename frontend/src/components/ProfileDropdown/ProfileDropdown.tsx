import { useUser } from '../../contexts/UserContext'
import { formatJoinDate } from '../../utils/format'

type ProfileDropdownProps = {
  onClose: () => void
}

export function ProfileDropdown({ onClose }: ProfileDropdownProps) {
  const { currentUser, clearUser } = useUser()

  if (!currentUser) return null

  const initials =
    (currentUser.firstName[0] ?? '').toUpperCase() +
    (currentUser.lastName[0] ?? '').toUpperCase()

  const handleSignOut = () => {
    clearUser()
    onClose()
  }

  return (
    <div className="profile-dropdown">
      <div className="profile-dropdown-header">
        <div className="profile-dropdown-avatar">{initials}</div>
        <div>
          <p className="profile-dropdown-name">
            {currentUser.firstName} {currentUser.lastName}
          </p>
          <span className={`profile-dropdown-role ${currentUser.role}`}>
            {currentUser.role}
          </span>
        </div>
      </div>

      <dl className="profile-dropdown-details">
        <div>
          <dt>Email</dt>
          <dd>{currentUser.email ?? '—'}</dd>
        </div>
        <div>
          <dt>Phone</dt>
          <dd>{currentUser.phoneNumber ?? '—'}</dd>
        </div>
        <div>
          <dt>Member since</dt>
          <dd>{formatJoinDate(currentUser.createdAt)}</dd>
        </div>
      </dl>

      <button type="button" className="profile-dropdown-signout" onClick={handleSignOut}>
        Sign out
      </button>
    </div>
  )
}
