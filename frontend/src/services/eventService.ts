import { getApiUrl } from '../utils/api'
import type { EventFilter, PagedEventResponse } from '../types'

export async function getActiveEvents(page: number, filter?: EventFilter): Promise<PagedEventResponse> {
  const params = new URLSearchParams({ page: String(page) })
  if (filter?.city)     params.set('city',     filter.city)
  if (filter?.category) params.set('category', filter.category)
  if (filter?.fromDate) params.set('fromDate', filter.fromDate)

  const response = await fetch(getApiUrl(`/api/events?${params}`))
  if (!response.ok) {
    const body = await response.text().catch(() => '')
    throw new Error(`Failed to load events (${response.status}): ${body}`)
  }
  return response.json() as Promise<PagedEventResponse>
}
