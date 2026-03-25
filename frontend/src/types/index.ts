export type AuthMode = 'login' | 'register'
export type ActiveTab = 'events' | 'my-events'

export type ApiErrorResponse = {
  detail?: string
  message?: string
}

export type PagedEventResponse = {
  events: Event[]
  hasMore: boolean
}

export type Event = {
  eventId: number
  title: string
  description: string
  eventDate: string
  availableTickets: number
  price: number
  status: string
  venueName: string
  venueCity: string
  categoryName: string
}
