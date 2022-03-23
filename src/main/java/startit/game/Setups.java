package startit.game;

import lombok.Getter;

@Getter
public enum Setups {
    START_TIMEOUT (5000),
    ROUND_TIMEOUT ( 5000),
    RESULT_TIMEOUT (5000),
    ROUNDS (3),
    USERS_PASSWORD( 1);

    private int value;
    Setups(int value) {
        this.value = value;
    }
}
