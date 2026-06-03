import { useState, useEffect } from 'react'
import { getSlowMode, setSlowMode, getWorkshop, setWorkshopCreditUsed, setWorkshopCreditLimit, resetDemoData, clearTrends } from '../api.js'
import { WORKSHOPS, fmtCop } from '../seed.js'

export default function AdminTab() {
  // --- Reiniciar demo ---
  const [resetting, setResetting] = useState(false)
  const [resetMsg, setResetMsg] = useState(null)
  const [confirming, setConfirming] = useState(false)
  // --- Proveedor lento ---
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

  // --- Credito del taller ---
  const [workshopId, setWorkshopIdInput] = useState('WS-002')
  const [workshop, setWorkshop] = useState(null)
  const [usedInput, setUsedInput] = useState('')
  const [limitInput, setLimitInput] = useState('')
  const [wsError, setWsError] = useState(null)

  const loadWorkshop = async (id = workshopId) => {
    setWsError(null)
    try {
      const w = await getWorkshop(id)
      setWorkshop(w); setUsedInput(String(w.creditUsed)); setLimitInput(String(w.creditLimit))
    } catch (e) { setWsError(e.message); setWorkshop(null) }
  }
  useEffect(() => { loadWorkshop('WS-002') }, [])

  const applyUsed = async (amount) => {
    setWsError(null)
    try { await setWorkshopCreditUsed(workshopId, amount); await loadWorkshop() }
    catch (e) { setWsError(e.message) }
  }
  const applyLimit = async () => {
    setWsError(null)
    try { await setWorkshopCreditLimit(workshopId, parseFloat(limitInput)); await loadWorkshop() }
    catch (e) { setWsError(e.message) }
  }

  const doReset = async () => {
    setResetting(true); setResetMsg(null)
    try {
      await Promise.all([resetDemoData(), clearTrends(), setSlowMode(0)])
      setConfirming(false)
      setResetMsg('Listo: pedidos borrados, cupos restaurados y tendencias limpias.')
      await refreshSlow(); await loadWorkshop()
    } catch (e) { setResetMsg('Error: ' + e.message) }
    finally { setResetting(false) }
  }

  let pct = 0, state = 's-ok'
  if (workshop) {
    const l = parseFloat(workshop.creditLimit), u = parseFloat(workshop.creditUsed)
    pct = l > 0 ? (u / l) * 100 : 0
    state = workshop.exceeded ? 's-over' : pct >= 80 ? 's-warn' : 's-ok'
  }

  return (
    <>
      <div className="card">
        <h2>Reiniciar demo</h2>
        <p className="help">
          Deja el sistema como recien sembrado: borra todos los pedidos, restaura los cupos de los
          talleres al estado inicial y limpia las tendencias. Util antes de una sustentacion.
        </p>
        {!confirming ? (
          <button className="btn danger" onClick={() => { setConfirming(true); setResetMsg(null) }} disabled={resetting}>
            Reiniciar demo
          </button>
        ) : (
          <div className="alert warn" style={{marginTop:0}}>
            <span className="icon">!</span>
            <div>
              Se borraran todos los pedidos y se limpiaran las tendencias. ¿Continuar?
              <div style={{marginTop:10, display:'flex', gap:8}}>
                <button className="btn danger" onClick={doReset} disabled={resetting}>
                  {resetting ? 'Reiniciando…' : 'Si, reiniciar'}
                </button>
                <button className="btn ghost" onClick={() => setConfirming(false)} disabled={resetting}>Cancelar</button>
              </div>
            </div>
          </div>
        )}
        {resetMsg && <div className="alert ok" style={{marginTop:12}}><span className="icon">✓</span><div>{resetMsg}</div></div>}
      </div>

      <div className="card">
        <h2>Simular proveedor lento</h2>
        <p className="help">
          Agrega un retardo artificial a las respuestas de los proveedores. Por encima de 800 ms,
          la busqueda marca la disponibilidad como incierta en lugar de hacer esperar al usuario.
        </p>

        <div className="row">
          <div>
            <label>Retardo actual</label>
            <input className="mono" value={`${delayMs} ms`} readOnly />
          </div>
          <div>
            <label>Nuevo retardo (ms)</label>
            <input type="number" min="0" value={delayInput} onChange={e => setDelayInput(e.target.value)} />
          </div>
          <button className="btn primary" onClick={() => applySlow(parseInt(delayInput, 10) || 0)}>Aplicar</button>
          <button className="btn ghost" onClick={() => applySlow(0)}>Quitar retardo</button>
        </div>

        {slowError && <div className="alert err"><span className="icon">×</span><div>{slowError}</div></div>}

        <p className="legend" style={{marginTop:14}}>
          Sugerencia: <code>0</code> = normal · <code>1500</code> = disponibilidad incierta.
        </p>
      </div>

      <div className="card">
        <h2>Cupo de credito del taller</h2>
        <p className="help">
          Ajusta el tope de credito y el credito usado de un taller. Los cambios se reflejan al
          instante en <strong>Talleres y bodegas</strong> y en <strong>Crear pedido</strong>.
        </p>

        <div className="row">
          <div>
            <label>Taller</label>
            <select className="mono" value={workshopId}
              onChange={e => { setWorkshopIdInput(e.target.value); loadWorkshop(e.target.value) }}>
              {WORKSHOPS.map(w => <option key={w.id} value={w.id}>{w.id} — {w.name}</option>)}
            </select>
          </div>
          <button className="btn ghost" onClick={() => loadWorkshop()}>Recargar</button>
        </div>

        {wsError && <div className="alert err"><span className="icon">×</span><div>{wsError}</div></div>}

        {workshop && (
          <>
            <div className={`gauge ${state}`}>
              <div className="g-head">
                <span className="g-name">{workshop.name}<span className="wid">{workshop.id}</span></span>
                <span className="g-pct">{Math.round(pct)}% del cupo</span>
              </div>
              <div className="g-track"><div className="g-fill" style={{ width: `${Math.min(100, pct)}%` }} /></div>
              <div className="g-foot">
                <span>Usado <span className="mono">{fmtCop(workshop.creditUsed)}</span> de <span className="mono">{fmtCop(workshop.creditLimit)}</span></span>
                {workshop.exceeded
                  ? <span className="badge NOT_FOUND">Cupo excedido</span>
                  : <span className="badge AVAILABLE">Cupo disponible</span>}
              </div>
            </div>

            <div className="row" style={{marginTop:16}}>
              <div>
                <label>Tope de credito (COP)</label>
                <input type="number" min="0" step="1000" value={limitInput} onChange={e => setLimitInput(e.target.value)} />
              </div>
              <button className="btn primary" onClick={applyLimit}>Aplicar tope</button>
            </div>

            <div className="row" style={{marginTop:12}}>
              <div>
                <label>Credito usado (COP)</label>
                <input type="number" min="0" step="1000" value={usedInput} onChange={e => setUsedInput(e.target.value)} />
              </div>
              <button className="btn primary" onClick={() => applyUsed(parseFloat(usedInput))}>Aplicar usado</button>
              <button className="btn ghost" onClick={() => applyUsed(0)}>Limpiar usado (0)</button>
            </div>
            <p className="legend">
              Para simular un taller bloqueado, pon el credito usado por encima del tope.
            </p>
          </>
        )}
      </div>
    </>
  )
}
