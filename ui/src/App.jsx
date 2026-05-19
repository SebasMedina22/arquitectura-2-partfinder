import { useState } from 'react'
import SearchTab from './tabs/SearchTab.jsx'
import OrderTab from './tabs/OrderTab.jsx'
import OrdersListTab from './tabs/OrdersListTab.jsx'
import TrendsTab from './tabs/TrendsTab.jsx'
import AdminTab from './tabs/AdminTab.jsx'

const TABS = [
  { id: 'search',   label: 'Buscar parte',         component: SearchTab },
  { id: 'order',    label: 'Crear pedido',         component: OrderTab },
  { id: 'orders',   label: 'Pedidos del taller',   component: OrdersListTab },
  { id: 'trends',   label: 'Tendencias',           component: TrendsTab },
  { id: 'admin',    label: 'Admin / simulacion',   component: AdminTab },
]

export default function App() {
  const [active, setActive] = useState('search')
  const ActiveComp = TABS.find(t => t.id === active).component

  return (
    <div className="app">
      <header className="topbar">
        <div className="brand">
          <div className="mark" aria-hidden="true" />
          <h1>PartFinder</h1>
          <span className="sub">Marketplace de repuestos · caso 7</span>
        </div>
      </header>

      <nav className="tabs" role="tablist">
        {TABS.map(t => (
          <button
            key={t.id}
            role="tab"
            aria-selected={t.id === active}
            className={t.id === active ? 'active' : ''}
            onClick={() => setActive(t.id)}
          >
            {t.label}
          </button>
        ))}
      </nav>

      <ActiveComp />
    </div>
  )
}
