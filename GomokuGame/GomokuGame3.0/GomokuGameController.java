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
    private List<GameMove> moveHistory = new ArrayList<>();
    private int undoCount = 0;
    private boolean undoRequested = false;
    private String undoRequestedBy = null;


    @MessageMapping("/undo-request")
    @SendTo("/topic/undo-request")
    public GameMove requestUndo(String player) {
        if (undoCount >= 3 || moveHistory.size() < 2) {
            return createErrorMove("无法悔棋：已达到最大悔棋次数或棋局刚开始");
        }
        undoRequested = true;
        undoRequestedBy = player;
        GameMove response = new GameMove();
        response.setMessage(player + " 请求悔棋");
        return response;
    }

    @MessageMapping("/undo-response")
    @SendTo("/topic/game")
    public GameMove respondToUndo(boolean accepted, String respondingPlayer) {
        if (!undoRequested) {
            return createErrorMove("没有待处理的悔棋请求");
        }
        if (respondingPlayer.equals(undoRequestedBy)) {
            return createErrorMove("不能回应自己的悔棋请求");
        }
        if (accepted) {
            undoCount++;
            List<GameMove> undoneMovesForResponse = new ArrayList<>();
            for (int i = 0; i < 2 && !moveHistory.isEmpty(); i++) {
                GameMove lastMove = moveHistory.remove(moveHistory.size() - 1);
                String piece = gameBoard.getPiece(lastMove.getX(), lastMove.getY());
                gameBoard.undoMove(lastMove.getX(), lastMove.getY());
                lastMove.setUndonePlayer(piece);
                undoneMovesForResponse.add(lastMove);
            }
            gameBoard.switchPlayer();
            GameMove response = new GameMove();
            response.setMessage("悔棋成功");
            response.setNextPlayer(gameBoard.getCurrentPlayer());
            response.setUndoneMovesForResponse(undoneMovesForResponse);
            undoRequested = false;
            undoRequestedBy = null;
            return response;
        } else {
            undoRequested = false;
            undoRequestedBy = null;
            return createErrorMove("悔棋请求被拒绝");
        }
    }

    private GameMove createErrorMove(String message) {
        GameMove errorMove = new GameMove();
        errorMove.setInvalid(true);
        errorMove.setMessage(message);
        return errorMove;
    }



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

    if (gameBoard.getBoard()[move.getX()][move.getY()] != null) {
        move.setInvalid(true);
        move.setMessage("该位置已有棋子");
        return move;
    }

        if (gameBoard.makeMove(move.getX(), move.getY(), move.getPlayer())) {
            if (gameBoard.checkWin(move.getX(), move.getY(), move.getPlayer())) {
                move.setWinner(move.getPlayer());
            } else {
                gameBoard.switchPlayer();
                move.setNextPlayer(gameBoard.getCurrentPlayer());
            }
            moveHistory.add(move);
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

@MessageMapping("/reset")
@SendTo("/topic/game")
public GameMove resetGame() {
    gameBoard = new GameBoard();
    players.clear();
    moveHistory.clear();
    undoCount = 0;
    undoRequested = false;
    undoRequestedBy = null;
    GameMove resetMove = new GameMove();
    resetMove.setMessage("游戏已重置");
    return resetMove;
}
}