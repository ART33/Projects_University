public class StateNim extends State {
  public int coins;

  public StateNim() {
    coins = 13;
    player = 1;
  }

  public StateNim(StateNim state) {
    this.coins = state.coins;
    player = state.player;
  }

  public String toString() {
    String ret = String.valueOf(coins);
    return ret;
  }
}
