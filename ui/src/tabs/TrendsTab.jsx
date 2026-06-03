import { useState, useEffect, useRef } from 'react'
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Cell, LabelList } from 'recharts'
import { getTopTrends } from '../api.js'

const wsUrl = () =>
  `${location.protocol === 'https:' ? 'wss' : 'ws'}://${location.host}/api/trends/ws/trends`

export default function TrendsTab() {
  const [rows, setRows] = useState([])
  const [error, setError] = useState(null)
  const [loading, setLoading] = useState(false)
  const [loaded, setLoaded] = useState(false)
  const [live, setLive] = useState(false)
  const [fresh, setFresh] = useState(null)
  const wsRef = useRef(null)
  const aliveRef = useRef(true)

  const load = async () => {
    setLoading(true); setError(null)
    try { setRows(await getTopTrends(20)) }
    catch (e) { setError(e.message) }
    finally { setLoading(false); setLoaded(true) }
  }

  useEffect(() => {
    aliveRef.current = true
    load()
    const connect = () => {
      if (!aliveRef.current) return
      let ws
      try { ws = new WebSocket(wsUrl()) } catch { return }
      wsRef.current = ws
      ws.onopen = () => setLive(true)
      ws.onclose = () => { setLive(false); if (aliveRef.current) setTimeout(connect, 3000) }
      ws.onerror = () => { try { ws.close() } catch {} }
      ws.onmessage = (ev) => {
        try {
          const m = JSON.parse(ev.data)
          if (m.partQuery) { setFresh(m.partQuery); setTimeout(() => setFresh(null), 2800) }
        } catch {}
        load()
      }
    }
    connect()
    return () => { aliveRef.current = false; try { wsRef.current && wsRef.current.close() } catch {} }
  }, [])

  const chartH = Math.max(160, rows.length * 40 + 20)
  // Recharts pinta con atributos SVG: var(--css) no resuelve, usamos hex que igualan el tema.
  const C = { accent: '#4d9bf5', ok: '#46c267', text0: '#eef1f5', text1: '#c7ccd4', text2: '#8b93a1', border: '#2b3340', bg1: '#1b2027', bg2: '#232a33' }

  return (
    <div className="card">
      <h2>
        Tendencias de busqueda
        <span className={`live-dot ${live ? 'on' : ''}`} title={live ? 'Actualizacion en vivo activa' : 'Sin conexion en vivo'}>
          {live ? 'en vivo' : 'desconectado'}
        </span>
      </h2>
      <p className="help">
        Los repuestos mas buscados que ningun proveedor pudo ofrecer. Es la demanda insatisfecha
        que conviene cubrir. La lista se actualiza sola cuando entra una nueva busqueda sin resultados.
      </p>

      <div className="row" style={{marginBottom:8}}>
        <button className="btn ghost" onClick={load} disabled={loading}>
          {loading ? 'Cargando…' : 'Refrescar'}
        </button>
      </div>

      {error && <div className="alert err"><span className="icon">×</span><div>{error}</div></div>}

      {loading && rows.length === 0 && (
        <div className="bars">
          {[0,1,2,3,4].map(i => <div key={i} className="skeleton sk-bar" style={{ width: `${90 - i*14}%` }} />)}
        </div>
      )}

      {rows.length > 0 && (
        <div style={{ width: '100%', height: chartH, marginTop: 8 }}>
          <ResponsiveContainer width="100%" height="100%">
            <BarChart layout="vertical" data={rows} margin={{ top: 4, right: 36, bottom: 4, left: 8 }} barCategoryGap={10}>
              <CartesianGrid horizontal={false} stroke={C.border} />
              <XAxis type="number" allowDecimals={false} tick={{ fill: C.text2, fontSize: 11 }} stroke={C.border} />
              <YAxis type="category" dataKey="partQuery" width={150}
                tick={{ fill: C.text1, fontSize: 12, fontFamily: 'ui-monospace, monospace' }} stroke={C.border} />
              <Tooltip
                cursor={{ fill: C.bg2 }}
                contentStyle={{ background: C.bg1, border: `1px solid ${C.border}`, borderRadius: 6, fontSize: 12 }}
                labelStyle={{ color: C.text0 }}
                itemStyle={{ color: C.text1 }}
                formatter={(v) => [`${v} busquedas`, 'Sin resultado']} />
              <Bar dataKey="failCount" radius={[0, 4, 4, 0]} barSize={22} isAnimationActive={true}>
                {rows.map((r, i) => (
                  <Cell key={i} fill={fresh === r.partQuery ? C.ok : C.accent} />
                ))}
                <LabelList dataKey="failCount" position="right" fill={C.text0} fontSize={12} />
              </Bar>
            </BarChart>
          </ResponsiveContainer>
        </div>
      )}

      {!loading && !error && loaded && rows.length === 0 && (
        <div className="empty">
          <div className="e-mark">∅</div>
          <div className="e-title">Sin tendencias todavia</div>
          <div className="e-body">
            Busca un repuesto que no exista (por ejemplo <code>zzz-no-existe</code>) en <strong>Buscar repuesto</strong>.<br />
            Aparecera aqui solo, en uno o dos segundos.
          </div>
        </div>
      )}
    </div>
  )
}
