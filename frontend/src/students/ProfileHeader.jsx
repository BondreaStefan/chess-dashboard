
export default function ProfileHeader({ student }) {
  return (
    <div>
      <h1>{student.firstName} {student.lastName}</h1>
      <p>Email: {student.email}</p>
      <p>
        {student.lichessUsername && (
          <a
            href={`https://lichess.org/@/${student.lichessUsername}`}
            target="_blank"
            rel="noopener noreferrer"
          >
            Lichess: {student.lichessUsername}
          </a>
        )}
      </p>
      <p>
        {student.chessComUsername && (
          <a
            href={`https://www.chess.com/member/${student.chessComUsername}`}
            target="_blank"
            rel="noopener noreferrer"
          >
            Chess.com: {student.chessComUsername}
          </a>
        )}
      </p>
    </div>
  )
}