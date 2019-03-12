import os
import sys
import math
import time

class sat2sud():
    def __init__(self,fold):
        self.folder=fold
        print(self.folder)

    def sudoku_solver(self,output,fil):
        list=[]
        row=1
        col=0
        puzz=""
        for i in output.split():
            if int(i)>0:
                list.append(i)
        ma=math.sqrt(len(list))
        for i in list:
            if col==ma:
                col=0
                row+=1
                fil.write("%s\n"%(puzz))
                puzz=""
            col+=1
            dec=int(i)-ma*ma*(row-1)-ma*(col-1)
            puzz+="%s"%int(dec)
        fil.write("%s"%(puzz))
        fil.close()
        return 0

    def main(self):
        '1.1 Arguments check, input as folder'
        input=[]
        folder = ""

        file=os.listdir(os.getcwd())
        for i in sorted(file):
            fi=open(i)
            input.append(fi.read())
            fi.close()

        if self.folder == "miniSAT_encoded" or self.folder == "miniSAT_encoded/":
            dir="../Sudoku_solved"
        elif self.folder == "miniSAT_encoded4x4" or self.folder == "miniSAT_encoded4x4/":
            dir="../Sudoku_solved4x4"
        elif self.folder =="miniSAT_hard_encoded" or self.folder!="miniSAT_hard_encoded/":
            dir="../Sudoku_hard_solved"

        if not os.path.exists(dir):
            print("Making a new folder ...")
            os.makedirs(dir)
        else:
            print("Warming: Folder exists. And We will Use This One")
        os.chdir(dir)

        '1.2 Read input'
        title = "Grid_solved"
        output=""
        num=0
        solve=False
        time_set=[]
        for txt in input:
            strn=0
            strn=time.clock()
            for i in txt.splitlines():
                if i.startswith("SAT"):
                    solve=True
                    num += 1
                    fil_name = title + str(num).zfill(2) + ".txt"
                    fil=open(fil_name,"w")
                    #wont start another line until "\n"
                    fil.write("Grid %s solved\n"%num)
                else:
                    output=i
                    self.sudoku_solver(output,fil)
                    tot=time.clock()-strn
                    time_set.append(tot)
                    print("Time of converting encoded puzzle %s into solution: %f"%(fil_name,tot))
        print("The total time of converting encoded puzzles into solutions %f"%(sum(time_set)))
        print("The number of puzzle processed: %s"%(len(time_set)))
        print("The average time for each puzzle: %f"%(sum(time_set)/len(time_set)))
        return time_set
