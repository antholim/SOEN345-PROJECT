export type AuthMode = 'login' | 'register'
export type ActiveTab = 'events' | 'my-events'

export type EventFilter = {
  city: string
  category: string
  fromDate: string
}

export type ApiErrorResponse = {
  detail?: string
  message?: string
}

export type PagedEventResponse = {
  events: Event[]
  hasMore: boolean
}

export type Reservation = {
  reservationId: number
  eventTitle: string
  eventDate: string
  venueCity: string
  venueName: string
  categoryName: string
  numberOfTickets: number
  totalPrice: number
  status: string
  eventStatus: string
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
  venueAddress: string
  venueCapacity: number
  categoryName: string
}
