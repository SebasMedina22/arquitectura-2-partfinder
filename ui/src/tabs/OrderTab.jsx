import { useState } from 'react'
import { createOrder } from '../api.js'

export default function OrderTab() {
  const [form, setForm] = useState({
    workshopId: 'WS-001',
    partId: 'PRT-FO-001',
    supplierId: 'SUP-LIMA',
    quantity: '2',
    unitPrice: '35000',
    currency: 'COP',
  })
  const [result, setResult] = useState(null)
  const [error, setError] = useState(null)
  const [busy, setBusy] = useState(false)

  const change = (key) => (e) => setForm(f => ({ ...f, [key]: e.target.value }))

  const onSubmit = async () => {
    setBusy(true); setError(null); setResult(null)
    try {
      const payload = {
        workshopId: form.workshopId.toUpperCase(),
        partId: form.partId.toUpperCase(),
        supplierId: form.supplierId.toUpperCase(),
        quantity: parseInt(form.quantity, 10),
        unitPrice: parseFloat(form.unitPrice),
        currency: form.currency.toUpperCase(),
      }
      setResult(await createOrder(payload))
    } catch (e) {
      setError({ status: e.status, body: e.body, message: e.message })
    } finally { setBusy(false) }
  }

  return (
    <div className="card">
      <h2>Crear pedido</h2>
      <p className="help">
        Valida la regla <strong>R2</strong>: si el taller tiene su cupo de credito excedido,
        el pedido se rechaza con HTTP 422.
      </p>

      <div className="row">
        <div>
          <label>Workshop ID</label>
          <input className="mono" value={form.workshopId} onChange={change('workshopId')} />
        </div>
        <div>
          <label>Part ID</label>
          <input className="mono" value={form.partId} onChange={change('partId')} />
        </div>
        <div>
          <label>Supplier ID</label>
          <input className="mono" value={form.supplierId} onChange={change('supplierId')} />
        </div>
      </div>

      <div className="row" style={{marginTop:14}}>
        <div>
          <label>Cantidad</label>
          <input type="number" min="1" value={form.quantity} onChange={change('quantity')} />
        </div>
        <div>
          <label>Precio unitario</label>
          <input type="number" min="0" step="0.01" value={form.unitPrice} onChange={change('unitPrice')} />
        </div>
        <div>
          <label>Moneda</label>
          <input className="mono" value={form.currency} onChange={change('currency')} maxLength="3" />
        </div>
        <button className="btn primary" onClick={onSubmit} disabled={busy}>
          {busy ? 'Creando…' : 'Crear pedido'}
        </button>
      </div>

      {error && (
        <div className="alert err">
          <span className="icon">×</span>
          <div>
            <strong>HTTP {error.status}</strong> {error.body?.code ? <code style={{marginLeft:6}}>{error.body.code}</code> : null}
            <br />
            {error.body?.message || error.message}
          </div>
        </div>
      )}

      {result && (
        <>
          <div className="alert ok">
            <span className="icon">✓</span>
            <div>
              Pedido <code>{result.id.slice(0,8)}…</code> creado con estado{' '}
              <span className={`badge ${result.status}`}>{result.status}</span>
              <br />
              Total: <strong>{result.totalAmount} {result.currency}</strong>
            </div>
          </div>
          <details>
            <summary>Respuesta completa</summary>
            <pre>{JSON.stringify(result, null, 2)}</pre>
          </details>
        </>
      )}

      <p className="legend" style={{marginTop:18}}>
        Workshops sembrados: <code>WS-001</code> (cupo OK), <code>WS-002</code> (80% usado), <code>WS-003</code> (excedido → R2 bloquea).
      </p>
    </div>
  )
}
