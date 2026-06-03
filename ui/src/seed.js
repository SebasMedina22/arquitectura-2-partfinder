// Datos sembrados por los SeedDataRunner del backend (Aggregator + InventoryDirect).
// Se replican aqui para poblar selectores y el directorio visual, y asi probar la
// demo sin memorizar IDs. Si cambias el seed del backend, actualiza esto.

export const WORKSHOPS = [
  { id: 'WS-001', name: 'Taller La Esquina',      creditLimit: 5000000, creditUsed:  500000 },
  { id: 'WS-002', name: 'Mecanica San Jose',      creditLimit: 3000000, creditUsed: 2400000 },
  { id: 'WS-003', name: 'Auto Servicios Bolivar', creditLimit: 2000000, creditUsed: 2200000 },
]

export const PARTS = [
  { id: 'PRT-FO-001', name: 'Filtro de aceite Toyota Corolla 2018',        category: 'filtros',     price: 35000,   query: 'filtro' },
  { id: 'PRT-BG-002', name: 'Bujias NGK Iridium',                          category: 'bujias',      price: 180000,  query: 'bujias' },
  { id: 'PRT-PF-003', name: 'Pastillas de freno delanteras Honda Civic',   category: 'frenos',      price: 220000,  query: 'pastillas' },
  { id: 'PRT-EM-099', name: 'Embrague Mazda CX-5',                         category: 'transmision', price: 1200000, query: 'embrague' },
  { id: 'PRT-AC-010', name: 'Aceite motor sintetico 5W30 4L',             category: 'lubricantes', price: 110000,  query: 'aceite' },
]

export const SUPPLIERS = [
  { id: 'SUP-LIMA',   name: 'Bodega Lima',      city: 'Lima' },
  { id: 'SUP-MEDLN',  name: 'Bodega Medellin',  city: 'Medellin' },
  { id: 'SUP-CALI',   name: 'Bodega Cali',      city: 'Cali' },
  { id: 'SUP-BOGOTA', name: 'Bodega Bogota',    city: 'Bogota' },
]

// stock por (parte, proveedor) tal como lo siembra InventoryDirect
export const INVENTORY = [
  { partId: 'PRT-FO-001', supplierId: 'SUP-LIMA',   stock: 25 },
  { partId: 'PRT-FO-001', supplierId: 'SUP-MEDLN',  stock: 12 },
  { partId: 'PRT-FO-001', supplierId: 'SUP-CALI',   stock: 3 },
  { partId: 'PRT-BG-002', supplierId: 'SUP-LIMA',   stock: 50 },
  { partId: 'PRT-BG-002', supplierId: 'SUP-MEDLN',  stock: 30 },
  { partId: 'PRT-BG-002', supplierId: 'SUP-CALI',   stock: 18 },
  { partId: 'PRT-BG-002', supplierId: 'SUP-BOGOTA', stock: 40 },
  { partId: 'PRT-PF-003', supplierId: 'SUP-BOGOTA', stock: 8 },
  { partId: 'PRT-EM-099', supplierId: 'SUP-LIMA',   stock: 0 },
  { partId: 'PRT-EM-099', supplierId: 'SUP-MEDLN',  stock: 0 },
]

// ---- helpers ----
export const partById = (id) => PARTS.find(p => p.id === id)
export const workshopById = (id) => WORKSHOPS.find(w => w.id === id)
export const supplierById = (id) => SUPPLIERS.find(s => s.id === id)

// proveedores que tienen catalogada una parte (con su stock sembrado)
export const offersForPart = (partId) =>
  INVENTORY.filter(i => i.partId === partId)
    .map(i => ({ ...i, supplier: supplierById(i.supplierId) }))

// partes que maneja un proveedor
export const partsForSupplier = (supplierId) =>
  INVENTORY.filter(i => i.supplierId === supplierId)
    .map(i => ({ ...i, part: partById(i.partId) }))

export const creditPct = (w) => w.creditLimit > 0 ? (w.creditUsed / w.creditLimit) * 100 : 0
export const creditState = (w) => {
  const pct = creditPct(w)
  return pct >= 100 ? 's-over' : pct >= 80 ? 's-warn' : 's-ok'
}

export const fmtCop = (amount) =>
  new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP', maximumFractionDigits: 0 })
    .format(parseFloat(amount || 0))

// Etiquetas en español para los estados (la UI nunca muestra el enum crudo)
export const AVAILABILITY_LABEL = {
  AVAILABLE: 'Disponible',
  UNCERTAIN: 'Disponibilidad incierta',
  NOT_FOUND: 'No encontrado',
}
export const availabilityLabel = (a) => AVAILABILITY_LABEL[a] || a

export const ORDER_STATUS_LABEL = {
  CREATED: 'Creado',
  FULFILLED: 'Entregado',
  CANCELLED: 'Cancelado',
}
export const orderStatusLabel = (s) => ORDER_STATUS_LABEL[s] || s
