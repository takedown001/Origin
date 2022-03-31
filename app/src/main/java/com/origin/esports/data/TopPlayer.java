package com.origin.esports.data;


/**
 * Created by Takedown on 2/10/20.
 */
public class TopPlayer {

    private String playerName;
    private String playerWinning;
    private String position;

    public TopPlayer() {
    }

    public TopPlayer(String playerName, String playerWinning, String position) {
        this.playerName = playerName;
        this.playerWinning = playerWinning;
        this.position = position;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerWinning() {
        return playerWinning;
    }

    public void setPlayerWinning(String playerWinning) {
        this.playerWinning = playerWinning;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

}
