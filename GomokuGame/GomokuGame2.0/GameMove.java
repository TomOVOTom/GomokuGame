package com.example.gomoku;

public class GameMove {
    private int x;
    private int y;
    private String player;
    private boolean invalid;
    private String winner;
    private String nextPlayer;
    private String message;
    private int playersCount;
    private String currentPlayer;

    public String getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(String currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public int getPlayersCount() {
        return playersCount;
    }

    public void setPlayersCount(int playersCount) {
        this.playersCount = playersCount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    // Getters and setters
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public boolean isInvalid() {
        return invalid;
    }

    public void setInvalid(boolean invalid) {
        this.invalid = invalid;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public String getNextPlayer() {
        return nextPlayer;
    }

    public void setNextPlayer(String nextPlayer) {
        this.nextPlayer = nextPlayer;
    }
}