export type UserRole = 'CUSTOMER' | 'ADMIN'

export type UserRecord = {
  userId: number
  firstName: string
  lastName: string
  email: string | null
  phoneNumber: string | null
  role: UserRole
  createdAt: string
}
