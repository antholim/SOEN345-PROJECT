import { createContext, useContext, useState } from 'react'
import type { ReactNode } from 'react'
import type { UserRecord } from '../models/user'

type UserContextValue = {
  currentUser: UserRecord | null
  saveUser: (user: UserRecord) => void
  clearUser: () => void
}

const UserContext = createContext<UserContextValue | null>(null)

export function UserProvider({ children }: { children: ReactNode }) {
  const [currentUser, setCurrentUser] = useState<UserRecord | null>(() => {
    try {
      const stored = localStorage.getItem('currentUser')
      return stored ? (JSON.parse(stored) as UserRecord) : null
    } catch {
      return null
    }
  })

  const saveUser = (user: UserRecord) => {
    localStorage.setItem('currentUser', JSON.stringify(user))
    setCurrentUser(user)
  }

  const clearUser = () => {
    localStorage.removeItem('currentUser')
    setCurrentUser(null)
  }

  return (
    <UserContext.Provider value={{ currentUser, saveUser, clearUser }}>
      {children}
    </UserContext.Provider>
  )
}

// eslint-disable-next-line react-refresh/only-export-components
export function useUser(): UserContextValue {
  const ctx = useContext(UserContext)
  if (!ctx) throw new Error('useUser must be used within a UserProvider')
  return ctx
}
