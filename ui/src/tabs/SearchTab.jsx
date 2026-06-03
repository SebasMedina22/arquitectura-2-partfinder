import { useState } from 'react'
import { searchParts } from '../api.js'
import { PARTS, WORKSHOPS, availabilityLabel } from '../seed.js'

const THRESHOLD = 800 // ms — umbral para marcar la disponibilidad como incierta

export default function SearchTab({ session }) {
  const [query, setQuery] = useState('filtro')
  const [workshopId, setWorkshopId] = useState(session?.workshopId || 'WS-001')
  const [results, setResults] = useState([])
  const [error, setError] = useState(null)
  const [busy, setBusy] = useState(false)
  const [elapsedMs, setElapsedMs] = useState(null)
  const [searched, setSearched] = useState(false)

  const run = async (q = query, ws = workshopId) => {
    setBusy(true); setError(null); setResults([]); setElapsedMs(null); setSearched(true)
    const t0 = performance.now()
    try {
      setResults(await searchParts(q, ws))
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

  const anyUncertain = results.some(r => r.availability === 'UNCERTAIN')
  const over = elapsedMs !== null && elapsedMs > THRESHOLD
  const meterPct = elapsedMs === null ? 0 : Math.min(100, (elapsedMs / (THRESHOLD * 2)) * 100)

  return (
    <div className="card">
      <h2>Buscar repuesto</h2>
      <p className="help">
        Busca una pieza por nombre y mira que proveedores la tienen en stock, con su precio
        de referencia. Si un proveedor responde lento, te lo marcamos como disponibilidad incierta
        para que no te quedes esperando.
      </p>

      <div className="row">
        <div style={{flex:3}}>
          <label>Texto a buscar</label>
          <input value={query} onChange={e => setQuery(e.target.value)}
            onKeyDown={e => e.key === 'Enter' && !busy && query.trim() && run()}
            placeholder="filtro de aceite, bujias, pastillas..." />
        </div>
        <div>
          <label>Buscando como</label>
          {session?.role === 'admin' ? (
            <select className="mono" value={workshopId} onChange={e => setWorkshopId(e.target.value)}>
              {WORKSHOPS.map(w => <option key={w.id} value={w.id}>{w.id} — {w.name}</option>)}
            </select>
          ) : (
            <input className="mono" value={workshopId} readOnly title="Tu taller" />
          )}
        </div>
        <button className="btn primary" onClick={() => run()} disabled={busy || !query.trim()}>
          {busy ? 'Buscando…' : 'Buscar'}
        </button>
      </div>

      {/* Quick-picks del catalogo sembrado + un termino inexistente para R3 */}
      <div className="picker">
        {PARTS.map(p => (
          <button key={p.id} className={`pick ${query === p.query ? 'on' : ''}`}
            onClick={() => { setQuery(p.query); run(p.query) }} title={p.name}>
            {p.query}
          </button>
        ))}
        <button className="pick" onClick={() => { setQuery('zzz-no-existe'); run('zzz-no-existe') }}
          title="Pieza que ningun proveedor tiene">
          zzz-no-existe
        </button>
      </div>

      {/* Medidor de latencia (R1) */}
      {elapsedMs !== null && (
        <div className="meter">
          <div className="m-head">
            <span>Tiempo de respuesta {anyUncertain && <span className="badge UNCERTAIN" style={{marginLeft:6}}>Disponibilidad incierta</span>}</span>
            <span className="val">{elapsedMs} ms</span>
          </div>
          <div className="m-track">
            <div className={`m-fill ${over ? 'over' : 'ok'}`} style={{ width: `${meterPct}%` }} />
            <div className="m-threshold" style={{ left: '50%' }} />
          </div>
          <p className="m-foot">
            {anyUncertain
              ? <>Un proveedor esta respondiendo lento, asi que marcamos su disponibilidad como incierta. Puedes seguir adelante con la compra bajo tu criterio.</>
              : over
                ? <>La respuesta tardo un poco, pero los proveedores alcanzaron a confirmar el stock.</>
                : <>Respuesta rapida: disponibilidad confirmada.</>}
          </p>
        </div>
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

      {busy && (
        <div className="results">
          <div className="skeleton sk-row" />
          <div className="skeleton sk-row" />
          <div className="skeleton sk-row" />
        </div>
      )}

      {!busy && results.length > 0 && (
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
                <span className={`badge ${r.availability}`}>{availabilityLabel(r.availability)}</span>
                <span className="price">{formatPrice(r.referencePrice, r.currency)}</span>
              </div>
            </div>
          ))}
        </div>
      )}

      {!busy && results.length === 0 && !error && searched && (
        <div className="alert warn">
          <span className="icon">!</span>
          <div>
            Ningun proveedor tiene esta pieza ahora mismo. La registramos en
            <strong> Tendencias</strong> para que los proveedores la tengan en cuenta.
          </div>
        </div>
      )}

      {!busy && !searched && (
        <div className="empty">
          <div className="e-mark">⌕</div>
          <div className="e-title">Busca un repuesto para empezar</div>
          <div className="e-body">
            Escribe el nombre de una pieza o usa los atajos de arriba.
          </div>
        </div>
      )}
    </div>
  )
}
