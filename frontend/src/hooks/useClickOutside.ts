import { useEffect } from 'react'
import type { RefObject } from 'react'

export function useClickOutside(
  ref: RefObject<HTMLElement | null>,
  callback: () => void,
): void {
  useEffect(() => {
    const handler = (e: MouseEvent) => {
      if (ref.current && !ref.current.contains(e.target as Node)) {
        callback()
      }
    }
    document.addEventListener('mousedown', handler)
    return () => document.removeEventListener('mousedown', handler)
  }, [ref, callback])
}
