// Wrappers de API. En dev Vite proxea /api/*; en prod Nginx lo hace.

async function request(path, opts = {}) {
  const resp = await fetch(path, {
    headers: { 'Content-Type': 'application/json', ...(opts.headers || {}) },
    ...opts,
  })
  const text = await resp.text()
  let body
  try { body = text ? JSON.parse(text) : null } catch { body = text }
  if (!resp.ok) {
    const err = new Error(typeof body === 'object' ? (body.message || JSON.stringify(body)) : (body || resp.statusText))
    err.status = resp.status
    err.body = body
    throw err
  }
  return body
}

// ===== Aggregator =====
export const searchParts = (query, workshopId) => {
  const q = new URLSearchParams({ query })
  if (workshopId) q.append('workshopId', workshopId)
  return request(`/api/aggregator/search?${q}`)
}

export const createOrder = (payload) =>
  request('/api/aggregator/orders', { method: 'POST', body: JSON.stringify(payload) })

export const listWorkshopOrders = (workshopId) =>
  request(`/api/aggregator/orders?workshopId=${encodeURIComponent(workshopId)}`)

export const listSupplierOrders = (supplierId) =>
  request(`/api/aggregator/orders?supplierId=${encodeURIComponent(supplierId)}`)

export const fulfillOrder = (id) =>
  request(`/api/aggregator/orders/${encodeURIComponent(id)}/fulfill`, { method: 'POST' })

export const cancelOrder = (id) =>
  request(`/api/aggregator/orders/${encodeURIComponent(id)}/cancel`, { method: 'POST' })

export const getWorkshop = (id) =>
  request(`/api/aggregator/admin/workshops/${encodeURIComponent(id)}`)

export const setWorkshopCreditUsed = (id, amount) =>
  request(`/api/aggregator/admin/workshops/${encodeURIComponent(id)}/credit-used`,
    { method: 'POST', body: JSON.stringify({ amount }) })

export const setWorkshopCreditLimit = (id, amount) =>
  request(`/api/aggregator/admin/workshops/${encodeURIComponent(id)}/credit-limit`,
    { method: 'POST', body: JSON.stringify({ amount }) })

// Reinicio de demo: borra pedidos + restaura cupos sembrados, y limpia tendencias.
export const resetDemoData = () =>
  request('/api/aggregator/admin/reset-demo', { method: 'POST' })

export const clearTrends = () =>
  request('/api/trends/trends/reset', { method: 'POST' })

// ===== InventoryDirect (admin) =====
export const getInventory = (partId) =>
  request(`/api/inventory/inventory?partId=${encodeURIComponent(partId)}`)

export const getSlowMode = () =>
  request('/api/inventory/admin/slow-mode')

export const setSlowMode = (delayMs) =>
  request('/api/inventory/admin/slow-mode', { method: 'POST', body: JSON.stringify({ delayMs }) })

// ===== TrendCollector =====
export const getTopTrends = (limit = 10) =>
  request(`/api/trends/trends/top?limit=${limit}`)

// ===== Salud (Actuator) =====
// No usa request(): /actuator/health responde 503 cuando algo esta DOWN,
// pero el body sigue trayendo {status, components}. Queremos leerlo igual.
async function probe(path) {
  try {
    const resp = await fetch(path, { headers: { Accept: 'application/json' } })
    const body = await resp.json().catch(() => ({}))
    return { reachable: true, status: body.status || (resp.ok ? 'UP' : 'DOWN'), components: body.components || {} }
  } catch {
    return { reachable: false, status: 'DOWN', components: {} }
  }
}

export const aggregatorHealth = () => probe('/api/aggregator/actuator/health')
export const inventoryHealth  = () => probe('/api/inventory/actuator/health')
export const trendsHealth     = () => probe('/api/trends/actuator/health')
