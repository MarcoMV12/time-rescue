package GameStates;

public enum gameState {

    PLAYING, PAUSE, START, QUIT,
    LEVEL_COMPLETED,
    CHARACTER_SELECT,
    GAME_COMPLETED;

    public static gameState state = START;
}
