import { getApiUrl } from '../utils/api'
import type { PagedEventResponse } from '../types'

export async function getActiveEvents(page: number): Promise<PagedEventResponse> {
  const response = await fetch(getApiUrl(`/api/events?page=${page}`))
  if (!response.ok) {
    const body = await response.text().catch(() => '')
    throw new Error(`Failed to load events (${response.status}): ${body}`)
  }
  return response.json() as Promise<PagedEventResponse>
}
