import { useState, useEffect } from 'react'
import { aggregatorHealth, inventoryHealth, trendsHealth } from '../api.js'

// Pequeño tablero de salud del sistema distribuido.
// Sondea cada 5s los /actuator/health de los 3 MS. El estado de RabbitMQ
// (y ES/MySQL) sale de los components del health del Aggregator.
export default function HealthBar() {
  const [items, setItems] = useState(null)

  const poll = async () => {
    const [agg, inv, trd] = await Promise.all([
      aggregatorHealth(), inventoryHealth(), trendsHealth(),
    ])
    const comp = agg.components || {}
    const next = [
      { name: 'Aggregator',     kind: 'core',   up: agg.status === 'UP' },
      { name: 'InventoryDirect', kind: 'sync',  up: inv.status === 'UP' },
      { name: 'TrendCollector', kind: 'async',  up: trd.status === 'UP' },
    ]
    if (comp.rabbit)        next.push({ name: 'RabbitMQ',      kind: 'broker', up: comp.rabbit.status === 'UP' })
    if (comp.elasticsearch) next.push({ name: 'Elasticsearch', kind: 'search', up: comp.elasticsearch.status === 'UP' })
    if (comp.db)            next.push({ name: 'MySQL',          kind: 'store',  up: comp.db.status === 'UP' })
    setItems(next)
  }

  useEffect(() => {
    poll()
    const id = setInterval(poll, 5000)
    return () => clearInterval(id)
  }, [])

  return (
    <div className="health" role="status" aria-label="Estado del sistema">
      <span className="h-label">Sistema</span>
      {(items || [
        { name: 'Aggregator', kind: 'core' }, { name: 'InventoryDirect', kind: 'sync' },
        { name: 'TrendCollector', kind: 'async' },
      ]).map((s) => (
        <span className="h-item" key={s.name} title={items ? (s.up ? 'UP' : 'DOWN') : 'comprobando…'}>
          <span className={`h-dot ${items ? (s.up ? 'up' : 'down') : ''}`} />
          <span className="name">{s.name}</span>
          <span className="kind">{s.kind}</span>
        </span>
      ))}
    </div>
  )
}
