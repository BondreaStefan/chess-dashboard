import RatingChart from './RatingChart.jsx'

export default function StudentRow({ student }) {
  const { firstName, lastName, gamesLast14Days, activityLevel, lastPlayedAt, rating } = student

  const ACTIVITY_LABELS = {
    INACTIVE: 'Inactive',
    LOW: 'Low',
    ACTIVE: 'Active',
    VERY_ACTIVE: 'Very active',
  }

  const PLATFORM_LABELS = { LICHESS: 'Lichess', CHESS_COM: 'Chess.com' }

  const TIME_CLASS_LABELS = {
    ULTRA_BULLET: 'UltraBullet', BULLET: 'Bullet', BLITZ: 'Blitz',
    RAPID: 'Rapid', CLASSICAL: 'Classical', CORRESPONDENCE: 'Daily',
  }

  return (
    <tr>
      <td>{firstName} {lastName}</td>
      <td>{gamesLast14Days}</td>
      <td>
        <span className={`badge badge--${activityLevel.toLowerCase()}`}>
          {ACTIVITY_LABELS[activityLevel]}
        </span>
      </td>
      <td>
        {rating ? (
          <span className={`trend trend--${rating.trend.toLowerCase()}`}>
            {rating.current} {trendArrow(rating.trend)}
            {rating.change != null && ` (${formatChange(rating.change)})`}
          </span>
        ) : (
          '-'
        )}
      </td>
      <td>
        {rating ? (
          <div className="rating-chart">
            <RatingChart history={rating.history} trend={rating.trend} />
            <span className="rating-chart__caption">
              {PLATFORM_LABELS[rating.platform]} · {TIME_CLASS_LABELS[rating.timeClass]}
            </span>
          </div>
        ) : (
          '—'
        )}
      </td>
      <td>{lastPlayedAt ? formatDaysAgo(lastPlayedAt) : 'Never'}</td>
    </tr>
  )
}

function trendArrow(trend) {
  if (trend === 'UP') return '↑'
  if (trend === 'DOWN') return '↓'
  return '→'
}

function formatChange(change) {
  return change > 0 ? `+${change}` : `${change}`
}

function formatDaysAgo(isoDate) {
  const days = Math.floor((Date.now() - new Date(isoDate)) / (1000 * 60 * 60 * 24))
  if (days === 0) return 'Today'
  if (days === 1) return 'Yesterday'
  return `${days} days ago`
}