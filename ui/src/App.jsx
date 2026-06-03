import { useState } from 'react'
import SearchTab from './tabs/SearchTab.jsx'
import OrderTab from './tabs/OrderTab.jsx'
import OrdersListTab from './tabs/OrdersListTab.jsx'
import TrendsTab from './tabs/TrendsTab.jsx'
import AdminTab from './tabs/AdminTab.jsx'
import DirectoryTab from './tabs/DirectoryTab.jsx'
import SupplierOrdersTab from './tabs/SupplierOrdersTab.jsx'
import HealthBar from './components/HealthBar.jsx'
import Login from './components/Login.jsx'

const TABS = [
  { id: 'search',    label: 'Buscar parte',       component: SearchTab },
  { id: 'order',     label: 'Crear pedido',       component: OrderTab },
  { id: 'orders',    label: 'Pedidos del taller', component: OrdersListTab },
  { id: 'received',  label: 'Pedidos recibidos',  component: SupplierOrdersTab },
  { id: 'trends',    label: 'Tendencias',         component: TrendsTab },
  { id: 'directory', label: 'Talleres y bodegas', component: DirectoryTab },
  { id: 'admin',     label: 'Admin / simulacion', component: AdminTab },
]

// Pestañas visibles por rol (RBAC a nivel UI).
// Taller: busca, pide y cancela. Proveedor: recibe y entrega. Admin: todo + simulacion.
const ROLE_TABS = {
  mecanico:  ['search', 'order', 'orders', 'trends', 'directory'],
  proveedor: ['received', 'trends', 'directory'],
  admin:     ['search', 'order', 'orders', 'received', 'trends', 'directory', 'admin'],
}

const SESSION_KEY = 'partfinder.session'

export default function App() {
  const [session, setSession] = useState(() => {
    try { return JSON.parse(localStorage.getItem(SESSION_KEY)) } catch { return null }
  })

  const login = (s) => { localStorage.setItem(SESSION_KEY, JSON.stringify(s)); setSession(s) }
  const logout = () => { localStorage.removeItem(SESSION_KEY); setSession(null) }

  if (!session) return <Login onLogin={login} />

  return <Shell session={session} onLogout={logout} />
}

function Shell({ session, onLogout }) {
  const visibleTabs = TABS.filter(t => ROLE_TABS[session.role].includes(t.id))
  const [active, setActive] = useState(visibleTabs[0].id)
  const current = visibleTabs.find(t => t.id === active) || visibleTabs[0]
  const ActiveComp = current.component

  return (
    <div className="app">
      <header className="topbar">
        <div className="brand">
          <div className="mark" aria-hidden="true" />
          <h1>PartFinder</h1>
          <span className="sub">Marketplace de repuestos · caso 7</span>
        </div>
        <HealthBar />
        <div className="session">
          <div className="who">
            <span className="nm">{session.name}</span>
            <span className="rl">{session.role}{session.role === 'mecanico' ? ` · ${session.workshopId}` : session.role === 'proveedor' ? ` · ${session.supplierId}` : ''}</span>
          </div>
          <button className="btn ghost tiny" onClick={onLogout}>Salir</button>
        </div>
      </header>

      <nav className="tabs" role="tablist">
        {visibleTabs.map(t => (
          <button
            key={t.id}
            role="tab"
            aria-selected={t.id === current.id}
            className={t.id === current.id ? 'active' : ''}
            onClick={() => setActive(t.id)}
          >
            {t.label}
          </button>
        ))}
      </nav>

      <ActiveComp session={session} />
    </div>
  )
}
