package Checkers_Tactics.network;

public enum MessageType {
    START_GAME, // informacja o starcie
    MOVE_REQUEST, // gość prosi o wykonanie ruchu
    MOVE_APPLIED, //ruch został zaakceptowany
    MOVE_REJECTED, //ruch został odrzucony
    GAME_OVER, //gra się zakończyła
    OPPONENT_DISCONNECTED //drugi gracz utracił połączenie
}
