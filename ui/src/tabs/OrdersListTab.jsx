import { useState } from 'react'
import { listWorkshopOrders } from '../api.js'

export default function OrdersListTab() {
  const [workshopId, setWorkshopId] = useState('WS-001')
  const [rows, setRows] = useState([])
  const [error, setError] = useState(null)

  const load = async () => {
    setError(null)
    try { setRows(await listWorkshopOrders(workshopId)) }
    catch (e) { setError(e.message) }
  }

  return (
    <div className="card">
      <h2>Pedidos del taller</h2>
      <p className="help">GET /orders?workshopId=… (Aggregator)</p>

      <div className="row">
        <div>
          <label>Workshop ID</label>
          <input className="mono" value={workshopId} onChange={e => setWorkshopId(e.target.value.toUpperCase())} />
        </div>
        <button className="btn primary" onClick={load}>Buscar</button>
      </div>

      {error && <div className="alert err"><span className="icon">×</span><div>{error}</div></div>}

      {rows.length > 0 ? (
        <table style={{ marginTop: 16 }}>
          <thead>
            <tr>
              <th>Order ID</th>
              <th>Parte</th>
              <th>Proveedor</th>
              <th>Cant.</th>
              <th>Total</th>
              <th>Estado</th>
              <th>Creado</th>
            </tr>
          </thead>
          <tbody>
            {rows.map(r => (
              <tr key={r.id}>
                <td><code>{r.id.slice(0,8)}…</code></td>
                <td><code>{r.partId}</code></td>
                <td><code>{r.supplierId}</code></td>
                <td>{r.quantity}</td>
                <td>{r.totalAmount} <span className="muted">{r.currency}</span></td>
                <td><span className={`badge ${r.status}`}>{r.status}</span></td>
                <td className="muted">{new Date(r.createdAt).toLocaleString()}</td>
              </tr>
            ))}
          </tbody>
        </table>
      ) : (!error && <p className="legend" style={{marginTop:12}}>Sin pedidos todavia.</p>)}
    </div>
  )
}
