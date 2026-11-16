import './RatingChart.css'
import { Area, AreaChart, Tooltip, YAxis } from 'recharts'

const TREND_COLORS = {
  UP: '#15803d',
  DOWN: '#b91c1c',
  STABLE: '#6b7280',
}

export default function RatingChart({ history, trend }) {
  if (history.length < 2) {
    return <span className="rating-chart__empty">Not enough games</span>
  }

  const color = TREND_COLORS[trend]
  const gradientId = `rating-gradient-${trend}`

  return (
    <AreaChart width={140} height={40} data={history}>
      <defs>
        <linearGradient id={gradientId} x1="0" y1="0" x2="0" y2="1">
          <stop offset="0%" stopColor={color} stopOpacity={0.8} />
          <stop offset="100%" stopColor={color} stopOpacity={0.10} />
        </linearGradient>
      </defs>
      <YAxis hide domain={['dataMin - 10', 'dataMax + 10']} />
      <Tooltip content={<ChartTooltip />} />
      <Area
        type="monotone"
        dataKey="rating"
        stroke={color}
        strokeWidth={2}
        fill={`url(#${gradientId})`}
        dot={false}
        isAnimationActive={false}
      />
    </AreaChart>
  )
}

function ChartTooltip({ active, payload }) {
  if (!active || !payload?.length) {
    return null
  }
  const point = payload[0].payload
  return (
    <div className="rating-chart__tooltip">
      <strong>{point.rating}</strong>
      <span>{new Date(point.playedAt).toLocaleDateString()}</span>
    </div>
  )
}