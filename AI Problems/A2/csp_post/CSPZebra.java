import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;

public class CSPZebra extends CSP {

  static Set<Object> VarColour = new HashSet<Object>(Arrays.asList(new String[]{"red","green","ivory","yellow","blue"}));
	static Set<Object> VarDrink = new HashSet<Object>(Arrays.asList(new String[]{"coffee","tea","milk","orange-juice","water"}));
	static Set<Object> VarNationality = new HashSet<Object>(Arrays.asList(new String[]{"englishman","spaniard","ukrainian","norwegian","japanese"}));
	static Set<Object> VarPet = new HashSet<Object>(Arrays.asList(new String[]{"dog","snails","fox","horse","zebra"}));
	static Set<Object> VarCigarette = new HashSet<Object>(Arrays.asList(new String[]{"old-gold","kools","chesterfield","lucky-strike","parliament"}));

  public boolean isGood(Object X, Object Y, Object x, Object y) {
    boolean good = true;
    //if X is not even mentioned in by the constraints, just return true
		//as nothing can be violated
		if(!C.containsKey(X))
			good = true;

      //check to see if there is an arc between X and Y
		//if there isn't an arc, then no constraint, i.e. it is good
		if(!C.get(X).contains(Y))
			good = true;

    int XLocation = Integer.valueOf(x.toString());
		int YLocation = Integer.valueOf(y.toString());

    if(X.equals("englishman")&&Y.equals("red")&&!x.equals(y))
			good = false;
		if(X.equals("spaniard")&&Y.equals("dog")&&!x.equals(y))
			good = false;
		if(X.equals("coffee")&&Y.equals("green")&&!x.equals(y))
			good = false;
		if(X.equals("ukrainian")&&Y.equals("tea")&&!x.equals(y))
			good = false;
    if(X.equals("ivory")&&Y.equals("green")&&!(YLocation-XLocation==1)) //Y locates at the right of X
			good = false;
		if(X.equals("old-gold")&&Y.equals("snails")&&!x.equals(y))
			good = false;
		if(X.equals("kools")&&Y.equals("yellow")&&!x.equals(y))
			good = false;
		if(X.equals("chesterfield")&&Y.equals("fox")&&!(YLocation-XLocation==1)&&!(XLocation-YLocation==1)) //X lives next to Y
			good = false;
		if(X.equals("kools")&&Y.equals("horse")&&!(YLocation-XLocation==1)&&!(XLocation-YLocation==1)) //X lives next to Y
			good = false;
		if(X.equals("lucky-strike")&&Y.equals("orange-juice")&&!x.equals(y))
			good = false;
		if(X.equals("japanese")&&Y.equals("parliament")&&!x.equals(y))
			good = false;
		if(X.equals("norwegian")&&Y.equals("blue")&&!(YLocation-XLocation==1)&&!(XLocation-YLocation==1)) //X lives next to Y
			good = false;

    if(VarColour.contains(X)&&VarColour.contains(Y)&&!X.equals(Y)&&x.equals(y))
			good = false;
		if(VarDrink.contains(X)&&VarDrink.contains(Y)&&!X.equals(Y)&&x.equals(y))
			good = false;
		if(VarNationality.contains(X)&&VarNationality.contains(Y)&&!X.equals(Y)&&x.equals(y))
			good = false;
		if(VarPet.contains(X)&&VarPet.contains(Y)&&!X.equals(Y)&&x.equals(y))
			good = false;
		if(VarCigarette.contains(X)&&VarCigarette.contains(Y)&&!X.equals(Y)&&x.equals(y))
			good = false;

    return good;
  }

  public static void main(String[] args) throws Exception {
    CSPZebra csp = new CSPZebra();

    Integer[] Domain = {1,2,3,4,5};

    for(Object X : VarColour)
			csp.addDomain(X, Domain);
		for(Object X : VarDrink)
			csp.addDomain(X, Domain);
		for(Object X : VarNationality)
			csp.addDomain(X, Domain);
		for(Object X : VarPet)
			csp.addDomain(X, Domain);
		for(Object X : VarCigarette)
			csp.addDomain(X, Domain);

    csp.D.get("milk").remove(1);
		csp.D.get("milk").remove(2);
		csp.D.get("milk").remove(4);
		csp.D.get("milk").remove(5);
		csp.D.get("norwegian").remove(2);
		csp.D.get("norwegian").remove(3);
		csp.D.get("norwegian").remove(4);
		csp.D.get("norwegian").remove(5);

    csp.addBidirectionalArc("englishman", "red");
    csp.addBidirectionalArc("spaniard", "dog");
    csp.addBidirectionalArc("coffee", "green");
    csp.addBidirectionalArc("ukrainian", "tea");
    csp.addBidirectionalArc("ivory", "green");// 5
    csp.addBidirectionalArc("old-gold", "snails");
    csp.addBidirectionalArc("kools", "yellow");
    csp.addBidirectionalArc("chesterfield","fox"); //10
    csp.addBidirectionalArc("kools","horse"); //11
    csp.addBidirectionalArc("lucky-strike", "orange-juice");
    csp.addBidirectionalArc("japanese", "parliament");
    csp.addBidirectionalArc("norwegian","blue"); //14

    for(Object X: VarColour)
			for(Object Y: VarColour)
				csp.addBidirectionalArc(X, Y);
		for(Object X: VarDrink)
			for(Object Y: VarDrink)
				csp.addBidirectionalArc(X, Y);
		for(Object X: VarNationality)
			for(Object Y: VarNationality)
				csp.addBidirectionalArc(X, Y);
		for(Object X: VarPet)
			for(Object Y: VarPet)
				csp.addBidirectionalArc(X, Y);
		for(Object X: VarCigarette)
			for(Object Y: VarCigarette)
				csp.addBidirectionalArc(X, Y);

		Search search = new Search(csp);
		System.out.println(search.BacktrackingSearch());
  }
}
