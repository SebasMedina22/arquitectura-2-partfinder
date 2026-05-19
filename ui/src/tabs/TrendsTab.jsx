import { useState, useEffect } from 'react'
import { getTopTrends } from '../api.js'

export default function TrendsTab() {
  const [rows, setRows] = useState([])
  const [error, setError] = useState(null)
  const [loading, setLoading] = useState(false)

  const load = async () => {
    setLoading(true); setError(null)
    try { setRows(await getTopTrends(20)) }
    catch (e) { setError(e.message) }
    finally { setLoading(false) }
  }

  useEffect(() => { load() }, [])

  return (
    <div className="card">
      <h2>Tendencias de busquedas fallidas</h2>
      <p className="help">
        Top de queries que ningun proveedor pudo satisfacer. Alimentado asincronamente por el
        evento <code>SearchFailedEvent</code> que publica el Aggregator cada vez que una busqueda
        devuelve <span className="badge NOT_FOUND" style={{margin:'0 4px'}}>NOT_FOUND</span> (R3).
      </p>

      <div className="row" style={{marginBottom:8}}>
        <button className="btn ghost" onClick={load} disabled={loading}>
          {loading ? 'Cargando…' : 'Refrescar'}
        </button>
      </div>

      {error && <div className="alert err"><span className="icon">×</span><div>{error}</div></div>}

      {rows.length > 0 ? (
        <table style={{ marginTop: 8 }}>
          <thead>
            <tr>
              <th>Query buscada</th>
              <th>Fallos</th>
              <th>Primera vez</th>
              <th>Ultima vez</th>
            </tr>
          </thead>
          <tbody>
            {rows.map((r, i) => (
              <tr key={i}>
                <td><code>{r.partQuery}</code></td>
                <td><strong>{r.failCount}</strong></td>
                <td className="muted">{new Date(r.firstSeen).toLocaleString()}</td>
                <td className="muted">{new Date(r.lastSeen).toLocaleString()}</td>
              </tr>
            ))}
          </tbody>
        </table>
      ) : (!error && !loading && (
        <p className="legend" style={{marginTop:12}}>
          Sin tendencias todavia. Genera una busqueda que no exista (ej. "xyz123") y vuelve.
        </p>
      ))}
    </div>
  )
}
