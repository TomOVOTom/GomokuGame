package com.example.gomoku;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class GomokuGameController {
    private GameBoard gameBoard = new GameBoard();

    @MessageMapping("/move")
    @SendTo("/topic/game")
    public GameMove makeMove(GameMove move) {
        if (gameBoard.makeMove(move.getX(), move.getY(), move.getPlayer())) {
            if (gameBoard.checkWin(move.getX(), move.getY(), move.getPlayer())) {
                move.setWinner(move.getPlayer());
            }
        } else {
            move.setInvalid(true);
        }
        return move;
    }
}