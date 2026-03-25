import { API_BASE_URL } from '../constants/api'

export const getApiUrl = (path: string): string =>
  API_BASE_URL ? `${API_BASE_URL}${path}` : path
