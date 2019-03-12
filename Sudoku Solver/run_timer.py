import sud2sat as sud
import sat2sud as sat
import sys
import os

def main():
    if len(sys.argv)==1:
        sys.exit("please input the puzzle file you want to solve")
    if len(sys.argv)>2:
        sys.exit("Argument Error: Program requires 1 arguement (%s given)"%(len(sys.argv)-1))
    print("\n=========================sud2sat Statistics===================\n")
    p1=sud.sud2sat(sys.argv[1])
    list1=p1.main()
    print("\n=========================sat2sud Statistics===================\n")
    folder=os.getcwd().split("/")[-1]
    p2=sat.sat2sud(folder)
    list2=p2.main()
    tot_time=[]
    print("\n===========================Summary============================\n")
    for i in range(len(list1)):
        tot_time.append(list1[i]+list2[i])
        print("THE TOTAL TIME OF SOLVIG PUZZLE %s : %f"%(i+1,tot_time[-1]))
    print("THE TOTAL TIME OF SOLVING ALL PUZZLE:%f"%(sum(tot_time)))
    print("the number of puzzles: %s"%(len(list1)))
    print("The average time of solving each puzzle: %f"%(sum(tot_time)/len(list1)))
    return 0

if __name__ == "__main__":
    main()
