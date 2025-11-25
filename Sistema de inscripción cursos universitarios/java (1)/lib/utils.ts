import { clsx, type ClassValue } from 'clsx'
import { twMerge } from 'tailwind-merge'

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs))
}

export const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8080'

export async function fetchJSON<T>(path: string, init?: RequestInit): Promise<T> {
  const hasBody = init && 'body' in init && (init as any).body !== undefined
  const headers = hasBody ? { 'Content-Type': 'application/json', ...(init?.headers || {}) } : init?.headers
  const res = await fetch(`${API_BASE_URL}${path}`, { ...(init || {}), headers, credentials: 'include' })
  if (!res.ok) {
    const text = await res.text()
    throw new Error(text || `HTTP ${res.status}`)
  }
  const ct = res.headers.get('content-type') || ''
  if (res.status === 204 || !ct.includes('application/json')) {
    return undefined as unknown as T
  }
  const txt = await res.text()
  if (!txt) return undefined as unknown as T
  return JSON.parse(txt) as T
}
