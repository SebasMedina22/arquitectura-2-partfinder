import { useState, useEffect } from 'react'
import { listSupplierOrders, fulfillOrder } from '../api.js'
import { fmtCop, orderStatusLabel, supplierById, workshopById } from '../seed.js'

// Vista del proveedor: pedidos dirigidos a SU bodega. Solo el proveedor entrega.
export default function SupplierOrdersTab({ session }) {
  const supplierId = session?.supplierId
  const [rows, setRows] = useState([])
  const [error, setError] = useState(null)
  const [busy, setBusy] = useState(false)
  const [loaded, setLoaded] = useState(false)
  const [acting, setActing] = useState(null)

  const load = async () => {
    setError(null); setBusy(true)
    try { setRows(await listSupplierOrders(supplierId)) }
    catch (e) { setError(e.message) }
    finally { setBusy(false); setLoaded(true) }
  }
  useEffect(() => { if (supplierId) load() }, [supplierId])

  const deliver = async (id) => {
    setActing(id); setError(null)
    try { await fulfillOrder(id); await load() }
    catch (e) { setError(e.message) }
    finally { setActing(null) }
  }

  const supplier = supplierById(supplierId)
  const pending = rows.filter(r => r.status === 'CREATED').length

  return (
    <div className="card">
      <h2>Pedidos recibidos</h2>
      <p className="help">
        Pedidos dirigidos a {supplier ? supplier.name : supplierId}. Confirma la entrega de cada
        pedido pendiente.
      </p>

      <div className="row" style={{marginBottom:8}}>
        <div className="kv">
          <dt>Bodega</dt><dd className="mono">{supplierId}</dd>
          <dt>Pendientes</dt><dd>{pending}</dd>
        </div>
        <button className="btn ghost" onClick={load} disabled={busy} style={{marginLeft:'auto'}}>
          {busy ? 'Cargando…' : 'Refrescar'}
        </button>
      </div>

      {error && <div className="alert err"><span className="icon">×</span><div>{error}</div></div>}

      {busy && <div style={{marginTop:8}}>{[0,1,2].map(i => <div key={i} className="skeleton sk-line" />)}</div>}

      {!busy && rows.length > 0 && (
        <table style={{ marginTop: 8 }}>
          <thead>
            <tr>
              <th>Pedido</th>
              <th>Taller</th>
              <th>Repuesto</th>
              <th>Cant.</th>
              <th>Total</th>
              <th>Estado</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {rows.map(r => {
              const w = workshopById(r.workshopId)
              return (
                <tr key={r.id}>
                  <td><code>{r.id.slice(0,8)}…</code></td>
                  <td>{w ? w.name : <code>{r.workshopId}</code>}</td>
                  <td><code>{r.partId}</code></td>
                  <td>{r.quantity}</td>
                  <td>{fmtCop(r.totalAmount)}</td>
                  <td><span className={`badge ${r.status}`}>{orderStatusLabel(r.status)}</span></td>
                  <td style={{textAlign:'right'}}>
                    {r.status === 'CREATED'
                      ? <button className="btn primary tiny" disabled={acting === r.id} onClick={() => deliver(r.id)}>Entregar</button>
                      : <span className="muted">—</span>}
                  </td>
                </tr>
              )
            })}
          </tbody>
        </table>
      )}

      {!busy && loaded && rows.length === 0 && !error && (
        <div className="empty">
          <div className="e-mark">▤</div>
          <div className="e-title">Sin pedidos recibidos</div>
          <div className="e-body">
            Cuando un taller te haga un pedido, aparecera aqui para que lo entregues.
          </div>
        </div>
      )}
    </div>
  )
}
