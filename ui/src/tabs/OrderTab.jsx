import { useState, useEffect } from 'react'
import { createOrder, getWorkshop } from '../api.js'
import { WORKSHOPS, PARTS, offersForPart, partById, fmtCop, orderStatusLabel } from '../seed.js'

export default function OrderTab({ session }) {
  const firstPart = PARTS[0]
  const [form, setForm] = useState({
    workshopId: session?.workshopId || 'WS-001',
    partId: firstPart.id,
    supplierId: offersForPart(firstPart.id)[0]?.supplierId || 'SUP-LIMA',
    quantity: '2',
    unitPrice: String(firstPart.price),
  })
  const [result, setResult] = useState(null)
  const [error, setError] = useState(null)
  const [busy, setBusy] = useState(false)
  const [workshop, setWorkshop] = useState(null)

  const set = (patch) => setForm(f => ({ ...f, ...patch }))

  const onPart = (partId) => {
    const offers = offersForPart(partId)
    const p = partById(partId)
    set({ partId, supplierId: offers[0]?.supplierId || '', unitPrice: p ? String(p.price) : form.unitPrice })
  }

  const loadWorkshop = async (id) => {
    if (!id) { setWorkshop(null); return }
    try { setWorkshop(await getWorkshop(id.toUpperCase())) }
    catch { setWorkshop(null) }
  }
  useEffect(() => {
    const t = setTimeout(() => loadWorkshop(form.workshopId), 250)
    return () => clearTimeout(t)
  }, [form.workshopId])

  const supplierOptions = offersForPart(form.partId)
  const selectedOffer = supplierOptions.find(o => o.supplierId === form.supplierId)
  const maxStock = selectedOffer ? selectedOffer.stock : 0
  const qty = parseInt(form.quantity, 10) || 0
  const overStock = qty > maxStock
  const canSubmit = !busy && form.supplierId && qty > 0 && !overStock

  const onSubmit = async () => {
    setBusy(true); setError(null); setResult(null)
    try {
      setResult(await createOrder({
        workshopId: form.workshopId,
        partId: form.partId,
        supplierId: form.supplierId,
        quantity: qty,
        unitPrice: parseFloat(form.unitPrice),
        currency: 'COP',
      }))
    } catch (e) {
      setError({ status: e.status, body: e.body, message: e.message })
    } finally {
      setBusy(false)
      loadWorkshop(form.workshopId)
    }
  }

  let gauge = null
  if (workshop) {
    const limit = parseFloat(workshop.creditLimit)
    const used = parseFloat(workshop.creditUsed)
    const pct = limit > 0 ? (used / limit) * 100 : 0
    const state = workshop.exceeded ? 's-over' : pct >= 80 ? 's-warn' : 's-ok'
    gauge = (
      <div className={`gauge ${state}`}>
        <div className="g-head">
          <span className="g-name">{workshop.name}<span className="wid">{workshop.id || form.workshopId}</span></span>
          <span className="g-pct">{Math.round(pct)}% del cupo</span>
        </div>
        <div className="g-track"><div className="g-fill" style={{ width: `${Math.min(100, pct)}%` }} /></div>
        <div className="g-foot">
          <span>Usado <span className="mono">{fmtCop(used)}</span> de <span className="mono">{fmtCop(limit)}</span></span>
          {workshop.exceeded
            ? <span className="badge NOT_FOUND">Cupo excedido</span>
            : <span className="badge AVAILABLE">Cupo disponible</span>}
        </div>
      </div>
    )
  }

  return (
    <div className="card">
      <h2>Crear pedido</h2>
      <p className="help">
        Emite un pedido a un proveedor a nombre de tu taller. El medidor muestra el cupo de
        credito disponible; si esta agotado, el pedido no se podra emitir.
      </p>

      <div className="row">
        <div>
          <label>Taller</label>
          {session?.role === 'admin' ? (
            <select className="mono" value={form.workshopId} onChange={e => set({ workshopId: e.target.value })}>
              {WORKSHOPS.map(w => <option key={w.id} value={w.id}>{w.id} — {w.name}</option>)}
            </select>
          ) : (
            <input className="mono" value={form.workshopId} readOnly title="Tu taller" />
          )}
        </div>
        <div style={{flex:2}}>
          <label>Repuesto</label>
          <select className="mono" value={form.partId} onChange={e => onPart(e.target.value)}>
            {PARTS.map(p => <option key={p.id} value={p.id}>{p.id} — {p.name}</option>)}
          </select>
        </div>
        <div>
          <label>Proveedor</label>
          <select className="mono" value={form.supplierId} onChange={e => set({ supplierId: e.target.value })}>
            {supplierOptions.length === 0 && <option value="">(sin proveedores)</option>}
            {supplierOptions.map(o => <option key={o.supplierId} value={o.supplierId}>{o.supplierId} · {o.stock}u disponibles</option>)}
          </select>
        </div>
      </div>

      {gauge}

      <div className="row" style={{marginTop:14}}>
        <div>
          <label>Cantidad {selectedOffer && <span className="muted" style={{textTransform:'none',letterSpacing:0}}>· max {maxStock}u</span>}</label>
          <input type="number" min="1" max={maxStock || 1} value={form.quantity} onChange={e => set({ quantity: e.target.value })} />
        </div>
        <div>
          <label>Precio unitario</label>
          <input type="number" min="0" step="0.01" value={form.unitPrice} onChange={e => set({ unitPrice: e.target.value })} />
        </div>
        <button className="btn primary" onClick={onSubmit} disabled={!canSubmit}>
          {busy ? 'Creando…' : 'Crear pedido'}
        </button>
      </div>

      {overStock && (
        <div className="alert warn" style={{marginTop:12}}>
          <span className="icon">!</span>
          <div>El proveedor {form.supplierId} solo tiene <strong>{maxStock}u</strong> en stock. Reduce la cantidad.</div>
        </div>
      )}

      {error && (
        <div className="alert err">
          <span className="icon">×</span>
          <div>
            {error.status === 422
              ? <>No se pudo emitir el pedido: {error.body?.message || 'el taller tiene el cupo de credito excedido.'}</>
              : <>{error.body?.message || error.message}</>}
          </div>
        </div>
      )}

      {result && (
        <div className="alert ok">
          <span className="icon">✓</span>
          <div>
            Pedido <code>{result.id.slice(0,8)}…</code> creado con estado{' '}
            <span className={`badge ${result.status}`}>{orderStatusLabel(result.status)}</span>
            <br />
            Total: <strong>{fmtCop(result.totalAmount)} {result.currency}</strong>
          </div>
        </div>
      )}

      <p className="legend" style={{marginTop:18}}>
        El taller <code>WS-003</code> tiene el cupo de credito agotado: usalo para ver el bloqueo de pedido.
      </p>
    </div>
  )
}
