import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class GameNim extends Game {
  int WinningScore = 10;
  int LosingScore = -10;

  public GameNim() {
    currentState = new StateNim();
  }

  public boolean isWinState(State state) {
    StateNim tstate = (StateNim) state;
    boolean isWin = false;
    if(tstate.coins == 1) {
      isWin = true;
    }
    return isWin;
  }

	public boolean isStuckState(State state) {
    return false;
  }

	public Set<State> getSuccessors(State state) {
    if(isWinState(state)) return null;

    Set<State> successors = new HashSet<State>();
    StateNim tstate = (StateNim) state;
    StateNim successor_state;

    for(int i = 0; i < 3; i++) {
      successor_state = new StateNim(tstate);
			successor_state.coins -= i+1;
			successor_state.player = (state.player==0 ? 1 : 0);
			successors.add(successor_state);
    }

    return successors;
  }

	public double eval(State state) {
    int Winner = 0;
    if(isWinState(state)) {
      int previous_player = (state.player == 0 ? 1 : 0);
      Winner = 0;

      if(previous_player == 0) {
        Winner = WinningScore;
      } else {
        Winner = LosingScore;
      }
    }
    return Winner;
  }

  public static void main(String[] args) throws Exception {
    Game game = new GameNim();
    Search search = new Search(game);
    int depth = 8;
    int CoinsAfterMove = 0;

    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

    while(true) {
      StateNim nextState = null;

      switch ( game.currentState.player ) {
        case 1:
          System.out.print("Enter *valid* number of coins you wan to remove> ");
          int num = Integer.parseInt( in.readLine() );

          if(num <= 0 || num >= 4) {
            System.out.println("Not valid.");
					  System.exit(1);
          }

          nextState = new StateNim((StateNim)game.currentState);
          nextState.player = 1;
          nextState.coins -= num;
          CoinsAfterMove = nextState.coins;
          System.out.println("Computer removed: \n" + num + " coins");
          System.out.println("Number of coins left: \n" + nextState);
          break;

        case 0:
          nextState = (StateNim)search.bestSuccessorState(depth);
          nextState.player = 0;
          System.out.println("Computer removed: \n" + (CoinsAfterMove -nextState.coins) + " coins");
				  System.out.println("Number of coins left: \n" + nextState);
          break;
      }

      game.currentState = nextState;
      //change player
      game.currentState.player = (game.currentState.player==0 ? 1 : 0);

      //Who wins?
      if ( game.isWinState(game.currentState) ) {

        if (game.currentState.player == 1) //i.e. last move was by the computer
          System.out.println("Computer wins!");
        else
          System.out.println("You win!");
          break;
      }
    }
  }
}
