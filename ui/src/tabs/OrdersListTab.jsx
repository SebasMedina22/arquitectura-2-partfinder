import { useState } from 'react'
import { listWorkshopOrders, cancelOrder } from '../api.js'
import { WORKSHOPS, fmtCop, orderStatusLabel } from '../seed.js'

export default function OrdersListTab({ session }) {
  const [workshopId, setWorkshopId] = useState(session?.workshopId || 'WS-001')
  const [rows, setRows] = useState([])
  const [error, setError] = useState(null)
  const [busy, setBusy] = useState(false)
  const [loaded, setLoaded] = useState(false)
  const [acting, setActing] = useState(null) // id en transicion

  const load = async () => {
    setError(null); setBusy(true)
    try { setRows(await listWorkshopOrders(workshopId)) }
    catch (e) { setError(e.message) }
    finally { setBusy(false); setLoaded(true) }
  }

  const act = async (id, fn) => {
    setActing(id); setError(null)
    try { await fn(id); await load() }
    catch (e) { setError(e.message) }
    finally { setActing(null) }
  }

  return (
    <div className="card">
      <h2>Pedidos del taller</h2>
      <p className="help">Consulta los pedidos de un taller y gestiona su estado.</p>

      <div className="row">
        <div>
          <label>Taller</label>
          {session?.role === 'admin' ? (
            <select className="mono" value={workshopId} onChange={e => setWorkshopId(e.target.value)}>
              {WORKSHOPS.map(w => <option key={w.id} value={w.id}>{w.id} — {w.name}</option>)}
            </select>
          ) : (
            <input className="mono" value={workshopId} readOnly title="Tu taller" />
          )}
        </div>
        <button className="btn primary" onClick={load} disabled={busy}>
          {busy ? 'Buscando…' : 'Buscar'}
        </button>
      </div>

      {error && <div className="alert err"><span className="icon">×</span><div>{error}</div></div>}

      {busy && (
        <div style={{marginTop:16}}>
          {[0,1,2].map(i => <div key={i} className="skeleton sk-line" />)}
        </div>
      )}

      {!busy && rows.length > 0 && (
        <table style={{ marginTop: 16 }}>
          <thead>
            <tr>
              <th>Pedido</th>
              <th>Repuesto</th>
              <th>Proveedor</th>
              <th>Cant.</th>
              <th>Total</th>
              <th>Estado</th>
              <th>Creado</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {rows.map(r => (
              <tr key={r.id}>
                <td><code>{r.id.slice(0,8)}…</code></td>
                <td><code>{r.partId}</code></td>
                <td><code>{r.supplierId}</code></td>
                <td>{r.quantity}</td>
                <td>{fmtCop(r.totalAmount)}</td>
                <td><span className={`badge ${r.status}`}>{orderStatusLabel(r.status)}</span></td>
                <td className="muted">{new Date(r.createdAt).toLocaleString()}</td>
                <td style={{whiteSpace:'nowrap', textAlign:'right'}}>
                  {r.status === 'CREATED'
                    ? <button className="btn danger tiny" disabled={acting === r.id}
                        onClick={() => act(r.id, cancelOrder)}>Cancelar</button>
                    : <span className="muted">—</span>}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}

      {!busy && loaded && rows.length === 0 && !error && (
        <div className="empty">
          <div className="e-mark">▤</div>
          <div className="e-title">Sin pedidos para {workshopId}</div>
          <div className="e-body">
            Crea uno en la pestaña <strong>Crear pedido</strong> y vuelve a buscar aqui.
          </div>
        </div>
      )}
    </div>
  )
}
