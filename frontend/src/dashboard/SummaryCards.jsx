import './SummaryCards.css'

export default function SummaryCards({ summary }) {
  return (
    <div className="summary-cards">
      <div className="summary-cards__item">
        <span className="summary-cards__label">Students</span>
        <span className="summary-cards__value">{summary.totalStudents}</span>
      </div>

      <div className="summary-cards__item">
        <span className="summary-cards__label">Active This Week</span>
        <span className="summary-cards__value">{summary.activeThisWeek}
          <span className="summary-cards__total"> / {summary.totalStudents}</span>
        </span>
      </div>

      <div className="summary-cards__item">
        <span className="summary-cards__label">Most Active</span>
        {summary.mostActive ? (
          <>
            <strong>{summary.mostActive.name}</strong>
            <span className="summary-cards__value">{summary.mostActive.games} games</span>
          </>
        ) : (
          <span className="summary-cards__value">None active</span>
        )}
      </div>
    </div>
  )
}