import { getApiUrl } from '../utils/api'

export async function getCategories(): Promise<string[]> {
  const response = await fetch(getApiUrl('/api/categories'))
  if (!response.ok) return []
  return response.json() as Promise<string[]>
}
