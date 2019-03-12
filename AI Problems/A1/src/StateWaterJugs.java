public class StateWaterJugs {
  int[] jugArray;

  public StateWaterJugs(int[] jugArray) {
    this.jugArray = jugArray;
  }

  public StateWaterJugs(StateWaterJugs state) {
    	jugArray = new int[3];
        for(int i = 0; i < 3; i++)
            this.jugArray[i] = state.jugArray[i];
    }

    public boolean equals(Object o)
    {
        StateWaterJugs state = (StateWaterJugs) o;
        boolean check = true;
        for (int i = 0; i < 3; i++)
            if (this.jugArray[i] != state.jugArray[i]) {
              check = false;
            }
        return check;
    }

    public int hashCode() {
        return this.jugArray[0]*100 + this.jugArray[1]*10 + this.jugArray[2];
    }

    public String toString()
    {
        String string = "";
        for (int i = 0; i < 3; i++)
            string += " " + this.jugArray[i];
        return string;
    }
}
