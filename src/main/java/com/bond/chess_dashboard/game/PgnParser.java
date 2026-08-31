package com.bond.chess_dashboard.game;

import com.github.bhlangonijr.chesslib.pgn.PgnIterator;
import com.bond.chess_dashboard.common.exception.InvalidPgnException;
import com.github.bhlangonijr.chesslib.game.Game;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class PgnParser{

    private static final DateTimeFormatter PGN_DATE = DateTimeFormatter.ofPattern("yyyy.MM.dd");
    private static final DateTimeFormatter PGN_TIME = DateTimeFormatter.ofPattern("HH:mm:ss");

    static ParsedGame parse(String pgn){
        PgnIterator pgnIterator = new PgnIterator(pgn.lines().toList());
        List<Game> games = new ArrayList<>();
        for(Game game : pgnIterator) {
            games.add(game);
        }

        if(games.isEmpty()) {
            throw new InvalidPgnException("No game found in PGN");
        }
        if(games.size() > 1) {
            throw new InvalidPgnException("PGN contains multiple games; only one is supported");
        }
        Game game = games.getFirst();

        String result = game.getResult().getDescription();

        String whiteName = game.getWhitePlayer().getName();

        String blackName = game.getBlackPlayer().getName();

        Integer whiteElo = game.getWhitePlayer().getElo() > 0 
                ? game.getWhitePlayer().getElo() 
                : null;

        Integer blackElo = game.getBlackPlayer().getElo() > 0
                ? game.getBlackPlayer().getElo()
                : null;
        
        OffsetDateTime playedAt = parsePlayedAt(game);


        return new ParsedGame(result, whiteName, blackName, whiteElo, blackElo, playedAt);
    }

    private static OffsetDateTime parsePlayedAt(Game game) {
    LocalDate date;
    try {
        date = LocalDate.parse(game.getRound().getEvent().getStartDate(), PGN_DATE);
    } catch (Exception e) {
        return null;
    }

    LocalTime time = LocalTime.MIDNIGHT;
    Map<String, String> props = game.getProperty();
    if (props != null) {
        // Lichess: UTCTime; chess.com: EndTime ("16:37:47 GMT+0000")
        String raw = props.getOrDefault("UTCTime", props.get("EndTime"));
        if (raw != null) {
            try {
                time = LocalTime.parse(raw.split(" ")[0], PGN_TIME);
            } catch (Exception e) {
                // rămâne miezul nopții
            }
        }
    }

    return date.atTime(time).atOffset(ZoneOffset.UTC);
}
    
}