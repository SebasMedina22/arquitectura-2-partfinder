import { useState } from 'react'
import { WORKSHOPS, SUPPLIERS } from '../seed.js'

// Login SIMULADO (RBAC a nivel UI, sin backend). En produccion iria con
// Spring Security + JWT y los roles vendrian del token. Aqui basta para la demo:
// el rol decide que pestañas se ven (el mecanico no ve Admin).
export default function Login({ onLogin }) {
  const [workshopId, setWorkshopId] = useState('WS-001')
  const [supplierId, setSupplierId] = useState(SUPPLIERS[0].id)

  const enterAsMechanic = () => {
    const w = WORKSHOPS.find(x => x.id === workshopId)
    onLogin({ role: 'mecanico', workshopId, name: w ? w.name : workshopId })
  }
  const enterAsSupplier = () => {
    const s = SUPPLIERS.find(x => x.id === supplierId)
    onLogin({ role: 'proveedor', supplierId, name: s ? s.name : supplierId })
  }
  const enterAsAdmin = () => onLogin({ role: 'admin', workshopId: 'WS-001', name: 'Operador' })

  return (
    <div className="login-wrap">
      <div className="login-card card">
        <div className="brand" style={{ marginBottom: 18 }}>
          <div className="mark" aria-hidden="true" />
          <h1 style={{ margin: 0, fontSize: 22, fontWeight: 600 }}>PartFinder</h1>
        </div>
        <p className="help" style={{ marginBottom: 22 }}>
          Marketplace de repuestos para talleres mecanicos. Selecciona con que perfil entras.
        </p>

        <div className="login-opt">
          <div className="lo-head">
            <span className="lo-title">Entrar como taller</span>
            <span className="badge AVAILABLE">mecanico</span>
          </div>
          <p className="lo-desc">Busca repuestos y crea pedidos a nombre de tu taller.</p>
          <div className="row" style={{ marginTop: 4 }}>
            <div>
              <label>Tu taller</label>
              <select className="mono" value={workshopId} onChange={e => setWorkshopId(e.target.value)}>
                {WORKSHOPS.map(w => <option key={w.id} value={w.id}>{w.id} — {w.name}</option>)}
              </select>
            </div>
            <button className="btn primary" onClick={enterAsMechanic}>Entrar</button>
          </div>
        </div>

        <div className="login-opt">
          <div className="lo-head">
            <span className="lo-title">Entrar como proveedor</span>
            <span className="badge CREATED">bodega</span>
          </div>
          <p className="lo-desc">Recibe los pedidos dirigidos a tu bodega y marcalos como entregados.</p>
          <div className="row" style={{ marginTop: 4 }}>
            <div>
              <label>Tu bodega</label>
              <select className="mono" value={supplierId} onChange={e => setSupplierId(e.target.value)}>
                {SUPPLIERS.map(s => <option key={s.id} value={s.id}>{s.id} — {s.name}</option>)}
              </select>
            </div>
            <button className="btn primary" onClick={enterAsSupplier}>Entrar</button>
          </div>
        </div>

        <div className="login-opt">
          <div className="lo-head">
            <span className="lo-title">Entrar como administrador</span>
            <span className="badge CREATED">admin</span>
          </div>
          <p className="lo-desc">Acceso total, incluido el panel de simulacion para preparar demostraciones.</p>
          <button className="btn ghost" onClick={enterAsAdmin} style={{ marginTop: 4 }}>Entrar como admin</button>
        </div>
      </div>
    </div>
  )
}
