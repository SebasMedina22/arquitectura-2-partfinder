import { useState } from 'react'
import { searchParts } from '../api.js'

export default function SearchTab() {
  const [query, setQuery] = useState('filtro')
  const [workshopId, setWorkshopId] = useState('WS-001')
  const [results, setResults] = useState([])
  const [error, setError] = useState(null)
  const [busy, setBusy] = useState(false)
  const [elapsedMs, setElapsedMs] = useState(null)

  const onSearch = async () => {
    setBusy(true); setError(null); setResults([]); setElapsedMs(null)
    const t0 = performance.now()
    try {
      const r = await searchParts(query, workshopId)
      setResults(r)
    } catch (e) {
      setError({ status: e.status, body: e.body, message: e.message })
    } finally {
      setElapsedMs(Math.round(performance.now() - t0))
      setBusy(false)
    }
  }

  const formatPrice = (amount, currency) =>
    new Intl.NumberFormat('es-CO', { style: 'currency', currency, maximumFractionDigits: 0 })
      .format(parseFloat(amount))

  return (
    <div className="card">
      <h2>Buscar parte en el catalogo</h2>
      <p className="help">
        Consulta el catalogo (Elasticsearch) + disponibilidad en proveedores via REST sincrono.
        Si InventoryDirect tarda mas de <code>800 ms</code>, el resultado se marca como
        <span className="badge UNCERTAIN" style={{margin:'0 4px'}}>UNCERTAIN</span>
        — regla R1 del caso.
      </p>

      <div className="row">
        <div style={{flex:3}}>
          <label>Texto a buscar</label>
          <input value={query} onChange={e => setQuery(e.target.value)} placeholder="filtro de aceite, bujias, pastillas..." />
        </div>
        <div>
          <label>Workshop ID (opcional)</label>
          <input className="mono" value={workshopId} onChange={e => setWorkshopId(e.target.value.toUpperCase())} placeholder="WS-001" />
        </div>
        <button className="btn primary" onClick={onSearch} disabled={busy || !query.trim()}>
          {busy ? 'Buscando…' : 'Buscar'}
        </button>
      </div>

      {elapsedMs !== null && (
        <p className="legend" style={{marginTop:14}}>
          Latencia total del request: <code>{elapsedMs} ms</code>
          {elapsedMs > 800 && <span> · supera el threshold de 800ms (R1 deberia haber marcado UNCERTAIN)</span>}
        </p>
      )}

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

      {results.length > 0 && (
        <div className="results">
          {results.map(r => (
            <div key={r.partId} className="result-row">
              <div>
                <p className="title">{r.partName}</p>
                <div className="meta">
                  <span className="pill">{r.partId}</span>
                  <span>{r.offers.length} {r.offers.length === 1 ? 'proveedor' : 'proveedores'}</span>
                  {r.offers.length > 0 && (
                    <span className="offers">
                      stock total {r.offers.reduce((s,o) => s + o.stock, 0)} u
                    </span>
                  )}
                </div>
                {r.offers.length > 0 && (
                  <div style={{marginTop:8, display:'flex', gap:8, flexWrap:'wrap'}}>
                    {r.offers.map(o => (
                      <span key={o.supplierId} className="pill" title={`${o.stock} unidades`}>
                        {o.supplierId} · {o.stock}u
                      </span>
                    ))}
                  </div>
                )}
              </div>
              <div className="right">
                <span className={`badge ${r.availability}`}>{r.availability}</span>
                <span className="price">{formatPrice(r.referencePrice, r.currency)}</span>
              </div>
            </div>
          ))}
        </div>
      )}

      {results.length === 0 && !error && !busy && elapsedMs !== null && (
        <div className="alert warn">
          <span className="icon">!</span>
          <div>
            Sin resultados en el catalogo. Se publico un <code>SearchFailedEvent</code> al broker
            (R3) — verifica la pestaña <strong>Tendencias</strong> en unos segundos.
          </div>
        </div>
      )}

      <p className="legend" style={{marginTop:16}}>
        Catalogo sembrado: <code>filtro</code> · <code>bujias</code> · <code>pastillas</code> · <code>embrague</code> · <code>aceite</code>.
        Para forzar R1 sube el slow-mode en la pestaña <strong>Admin</strong> a 1500ms.
      </p>
    </div>
  )
}
