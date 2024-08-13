package com.example.gomoku;

public class GameBoard {
    private static final int SIZE = 15;
    private String[][] board;
    private String currentPlayer = "X";

    public String getCurrentPlayer() {
        return currentPlayer;
    }

    public void switchPlayer() {
        currentPlayer = currentPlayer.equals("X") ? "O" : "X";
    }

    public GameBoard() {
        board = new String[SIZE][SIZE];
    }

    public boolean makeMove(int x, int y, String player) {
        if (x < 0 || x >= SIZE || y < 0 || y >= SIZE || board[x][y] != null) {
            return false;
        }
        board[x][y] = player;
        return true;
    }

    public boolean checkWin(int x, int y, String player) {
        return checkDirection(x, y, player, 1, 0) || // Horizontal
                checkDirection(x, y, player, 0, 1) || // Vertical
                checkDirection(x, y, player, 1, 1) || // Diagonal \
                checkDirection(x, y, player, 1, -1);  // Diagonal /
    }

    private boolean checkDirection(int x, int y, String player, int dx, int dy) {
        int count = 0;
        for (int i = -4; i <= 4; i++) {
            int nx = x + i * dx;
            int ny = y + i * dy;
            if (nx >= 0 && nx < SIZE && ny >= 0 && ny < SIZE && player.equals(board[nx][ny])) {
                count++;
                if (count == 5) {
                    return true;
                }
            } else {
                count = 0;
            }
        }
        return false;
    }

    public String[][] getBoard() {
        return board;
    }
}