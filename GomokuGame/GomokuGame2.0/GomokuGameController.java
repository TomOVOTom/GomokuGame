package com.example.gomoku;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

@Controller
public class GomokuGameController {
    private GameBoard gameBoard = new GameBoard();
    private List<String> players = new ArrayList<>();


      @MessageMapping("/move")
   @SendTo("/topic/game")
   public GameMove makeMove(GameMove move) {
       if (players.size() < 2) {
           move.setInvalid(true);
           move.setMessage("等待另一个玩家加入");
           return move;
       }

       if (!move.getPlayer().equals(gameBoard.getCurrentPlayer())) {
           move.setInvalid(true);
           move.setMessage("不是你的回合");
           return move;
       }

       if (gameBoard.makeMove(move.getX(), move.getY(), move.getPlayer())) {
           if (gameBoard.checkWin(move.getX(), move.getY(), move.getPlayer())) {
               move.setWinner(move.getPlayer());
           } else {
               gameBoard.switchPlayer();
               move.setNextPlayer(gameBoard.getCurrentPlayer());
           }
       } else {
           move.setInvalid(true);
       }
       return move;
   }

  @MessageMapping("/join")
@SendTo("/topic/players")
public GameMove joinGame() {
    GameMove response = new GameMove();
    if (players.size() < 2) {
        String player = players.isEmpty() ? "X" : "O";
        players.add(player);
        response.setPlayer(player);
        response.setMessage(player + " 已加入游戏");
        response.setCurrentPlayer("X"); // 游戏总是从 X 开始
        response.setPlayersCount(players.size());
    } else {
        response.setMessage("游戏已满，请等待");
    }
    return response;
}

//    @MessageMapping("/reset")
//    @SendTo("/topic/game")
//    public GameMove resetGame() {
//        gameBoard = new GameBoard();
//        players.clear();
//        GameMove resetMove = new GameMove();
//        resetMove.setMessage("游戏已重置");
//        return resetMove;
//    }
}