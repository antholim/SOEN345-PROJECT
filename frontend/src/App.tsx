import { UserProvider, useUser } from './contexts/UserContext'
import { AuthScreen } from './screens/AuthScreen/AuthScreen'
import { DashboardScreen } from './screens/DashboardScreen/DashboardScreen'
import './styles/auth.css'
import './styles/dashboard.css'

function AppContent() {
  const { currentUser } = useUser()
  return currentUser ? <DashboardScreen /> : <AuthScreen />
}

export default function App() {
  return (
    <UserProvider>
      <AppContent />
    </UserProvider>
  )
}
