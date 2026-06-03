import { useState, useEffect } from 'react'
import { getWorkshop } from '../api.js'
import { WORKSHOPS, SUPPLIERS, PARTS, offersForPart, partsForSupplier, fmtCop } from '../seed.js'

// Directorio: talleres con su cupo EN VIVO (consultado al backend), bodegas con su
// stock y el catalogo de repuestos. Apoyo visual del dominio.
export default function DirectoryTab() {
  const [workshops, setWorkshops] = useState(null)
  const [loading, setLoading] = useState(false)

  const load = async () => {
    setLoading(true)
    try {
      const live = await Promise.all(WORKSHOPS.map(w => getWorkshop(w.id).catch(() => null)))
      setWorkshops(live.map((w, i) => w || { ...WORKSHOPS[i], exceeded: false }))
    } finally { setLoading(false) }
  }
  useEffect(() => { load() }, [])

  const list = workshops || WORKSHOPS.map(w => ({ ...w, exceeded: false }))

  return (
    <div className="card">
      <h2>Talleres, bodegas y catalogo</h2>
      <p className="help">
        Vista general de los talleres clientes, las bodegas proveedoras y el catalogo de repuestos.
        El cupo de los talleres se muestra en tiempo real.
      </p>

      {/* Talleres */}
      <section className="dir-section">
        <div className="sec-head">
          <h3>Talleres mecanicos</h3>
          <span className="count">{list.length} clientes</span>
          <button className="btn ghost tiny" style={{marginLeft:'auto'}} onClick={load} disabled={loading}>
            {loading ? 'Actualizando…' : 'Actualizar'}
          </button>
        </div>
        <p className="sec-sub">Clientes del marketplace, con su cupo de credito disponible.</p>
        <div className="entity-grid">
          {list.map(w => {
            const limit = parseFloat(w.creditLimit)
            const used = parseFloat(w.creditUsed)
            const pct = limit > 0 ? (used / limit) * 100 : 0
            const state = w.exceeded || pct >= 100 ? 's-over' : pct >= 80 ? 's-warn' : 's-ok'
            return (
              <div className={`entity ${state}`} key={w.id}>
                <div className="e-top">
                  <span className="e-name">{w.name}</span>
                  <span className="e-id">{w.id}</span>
                </div>
                <div className="e-bar">
                  <div className="g-track"><div className="g-fill" style={{ width: `${Math.min(100, pct)}%` }} /></div>
                  <div className="e-barfoot">
                    <span className="mono">{fmtCop(used)} / {fmtCop(limit)}</span>
                    {(w.exceeded || pct >= 100)
                      ? <span className="badge NOT_FOUND">Cupo excedido</span>
                      : pct >= 80
                        ? <span className="badge UNCERTAIN">{Math.round(pct)}%</span>
                        : <span className="badge AVAILABLE">{Math.round(pct)}%</span>}
                  </div>
                </div>
              </div>
            )
          })}
        </div>
      </section>

      {/* Bodegas */}
      <section className="dir-section">
        <div className="sec-head">
          <h3>Bodegas proveedoras</h3>
          <span className="count">{SUPPLIERS.length} proveedores</span>
        </div>
        <p className="sec-sub">Cada bodega ofrece sus repuestos con el stock disponible.</p>
        <div className="entity-grid">
          {SUPPLIERS.map(s => {
            const parts = partsForSupplier(s.id)
            return (
              <div className="entity" key={s.id}>
                <div className="e-top">
                  <span className="e-name">{s.name}</span>
                  <span className="e-id">{s.id}</span>
                </div>
                <div className="e-meta">{parts.length} {parts.length === 1 ? 'repuesto' : 'repuestos'} en catalogo</div>
                <div className="chips">
                  {parts.map(p => (
                    <span className={`chip ${p.stock === 0 ? 'zero' : ''}`} key={p.partId} title={p.part?.name}>
                      {p.partId} · {p.stock}u
                    </span>
                  ))}
                </div>
              </div>
            )
          })}
        </div>
      </section>

      {/* Catalogo de partes */}
      <section className="dir-section">
        <div className="sec-head">
          <h3>Catalogo de repuestos</h3>
          <span className="count">{PARTS.length} repuestos</span>
        </div>
        <p className="sec-sub">Piezas que se pueden buscar y pedir en la plataforma.</p>
        <div className="entity-grid">
          {PARTS.map(p => {
            const offers = offersForPart(p.id)
            const total = offers.reduce((a, o) => a + o.stock, 0)
            const noStock = offers.length === 0 || total === 0
            return (
              <div className="entity" key={p.id}>
                <div className="e-top">
                  <span className="e-name">{p.name}</span>
                  <span className="e-id">{p.id}</span>
                </div>
                <div className="e-meta">
                  <span className="tag">{p.category}</span>
                  <span style={{ marginLeft: 8 }}>{fmtCop(p.price)}</span>
                </div>
                <div className="e-barfoot" style={{ marginTop: 11 }}>
                  <span>{offers.length} {offers.length === 1 ? 'bodega' : 'bodegas'}</span>
                  {noStock
                    ? <span className="badge NOT_FOUND">Sin stock</span>
                    : <span className="badge AVAILABLE">{total}u totales</span>}
                </div>
              </div>
            )
          })}
        </div>
      </section>
    </div>
  )
}
