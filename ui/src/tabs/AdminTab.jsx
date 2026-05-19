import { useState, useEffect } from 'react'
import { getSlowMode, setSlowMode, getWorkshop, setWorkshopCreditUsed } from '../api.js'

export default function AdminTab() {
  // --- Slow mode ---
  const [delayMs, setDelayMs] = useState(0)
  const [delayInput, setDelayInput] = useState('1500')
  const [slowError, setSlowError] = useState(null)

  const refreshSlow = async () => {
    try { const r = await getSlowMode(); setDelayMs(r.delayMs) }
    catch (e) { setSlowError(e.message) }
  }
  useEffect(() => { refreshSlow() }, [])

  const applySlow = async (ms) => {
    setSlowError(null)
    try { const r = await setSlowMode(ms); setDelayMs(r.delayMs) }
    catch (e) { setSlowError(e.message) }
  }

  // --- Workshop credit ---
  const [workshopId, setWorkshopIdInput] = useState('WS-002')
  const [workshop, setWorkshop] = useState(null)
  const [creditUsed, setCreditUsed] = useState('')
  const [wsError, setWsError] = useState(null)

  const loadWorkshop = async () => {
    setWsError(null); setWorkshop(null)
    try {
      const w = await getWorkshop(workshopId)
      setWorkshop(w); setCreditUsed(w.creditUsed.toString())
    } catch (e) { setWsError(e.message) }
  }
  const applyCredit = async () => {
    setWsError(null)
    try {
      await setWorkshopCreditUsed(workshopId, parseFloat(creditUsed))
      await loadWorkshop()
    } catch (e) { setWsError(e.message) }
  }

  return (
    <>
      <div className="card">
        <h2>Slow mode · InventoryDirect</h2>
        <p className="help">
          Inyecta un retardo artificial en cada <code>GET /inventory</code>. Subelo por encima de
          800ms para ver como el Aggregator marca los resultados como
          <span className="badge UNCERTAIN" style={{margin:'0 4px'}}>UNCERTAIN</span> (R1).
        </p>

        <div className="row">
          <div>
            <label>Delay actual</label>
            <input className="mono" value={`${delayMs} ms`} readOnly />
          </div>
          <div>
            <label>Nuevo delay (ms)</label>
            <input type="number" min="0" value={delayInput} onChange={e => setDelayInput(e.target.value)} />
          </div>
          <button className="btn primary" onClick={() => applySlow(parseInt(delayInput, 10) || 0)}>
            Aplicar
          </button>
          <button className="btn ghost" onClick={() => applySlow(0)}>
            Reset (0)
          </button>
        </div>

        {slowError && <div className="alert err"><span className="icon">×</span><div>{slowError}</div></div>}

        <p className="legend" style={{marginTop:14}}>
          Atajos: <code>0</code> = normal · <code>1500</code> = forzar UNCERTAIN · <code>3000</code> = casi seguro UNCERTAIN.
        </p>
      </div>

      <div className="card">
        <h2>Credito del taller</h2>
        <p className="help">
          Consulta y ajusta el cupo usado del taller. Sirve para forzar R2 en demos.
        </p>

        <div className="row">
          <div>
            <label>Workshop ID</label>
            <input className="mono" value={workshopId} onChange={e => setWorkshopIdInput(e.target.value.toUpperCase())} />
          </div>
          <button className="btn primary" onClick={loadWorkshop}>Cargar</button>
        </div>

        {wsError && <div className="alert err"><span className="icon">×</span><div>{wsError}</div></div>}

        {workshop && (
          <>
            <dl className="kv" style={{marginTop:18}}>
              <dt>Nombre</dt>          <dd>{workshop.name}</dd>
              <dt>Cupo limite</dt>     <dd>{workshop.creditLimit} {workshop.currency}</dd>
              <dt>Cupo usado</dt>      <dd>{workshop.creditUsed} {workshop.currency}</dd>
              <dt>Disponible</dt>      <dd>{workshop.creditAvailable} {workshop.currency}</dd>
              <dt>Excedido</dt>        <dd>
                {workshop.exceeded
                  ? <span className="badge NOT_FOUND">SI</span>
                  : <span className="badge AVAILABLE">NO</span>}
              </dd>
            </dl>

            <div className="row" style={{marginTop:14}}>
              <div>
                <label>Nuevo "credito usado" para simulacion</label>
                <input type="number" min="0" step="0.01" value={creditUsed} onChange={e => setCreditUsed(e.target.value)} />
              </div>
              <button className="btn primary" onClick={applyCredit}>Aplicar</button>
            </div>
            <p className="legend">
              Pone el valor exacto que recibe. Para forzar R2, asignar un valor &gt;= cupo limite.
            </p>
          </>
        )}
      </div>
    </>
  )
}
