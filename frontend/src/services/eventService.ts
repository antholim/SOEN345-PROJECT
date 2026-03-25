import { getApiUrl } from '../utils/api'
import type { Event } from '../types'

export async function getActiveEvents(): Promise<Event[]> {
  const response = await fetch(getApiUrl('/api/events'))
  if (!response.ok) throw new Error('Failed to load events.')
  return response.json() as Promise<Event[]>
}
