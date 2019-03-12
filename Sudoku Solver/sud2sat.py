import sys
import os
import math
import shutil
import time

class sud2sat():
    def __init__(self, puzzle_file):
        self.puzzle=puzzle_file

    def toDecimal(self,i, j, k, row):
        return (row**2)*(i-1)+row*(j-1)+k

    def constraint01(self,output, row, fil):
        for i in range(1,row+1):
            for j in range(1,row+1):
                list = ""
                if int(output[i-1][j-1]) not in range(1,row+1):
                    for k in range(1, row+1):
                        list += "%s "%(self.toDecimal(i,j,k,row))
                else:
                    list = "%s "%(self.toDecimal(i,j,int(output[i-1][j-1]),row))
                fil.write("%s%s\n"%(list,0))
        return 0

    def constraint02(self,output,row,fil):
        for i in range(1,row+1):
            for k in range(1,row+1):
                for j in range(1,row):
                    for l in range(j+1,row+1):
                        fil.write("-%s -%s 0\n"%(self.toDecimal(j,i,k,row),self.toDecimal(l,i,k,row)))
        return 0

    def constraint03(self,output,row,fil):
        for j in range(1,row+1):
            for k in range(1,row+1):
                for i in range(1,row):
                    for l in range(i+1,row+1):
                        fil.write("-%s -%s 0\n"%(self.toDecimal(j,i,k,row),self.toDecimal(j,l,k,row)))
        return 0

    def constraint04(self,output,row,fil):
        ma=int(math.sqrt(row))
        for k in range(1,row+1):
            for a in range(0,ma):
                for b in range(0,ma):
                    for u in range(1,ma+1):
                        for v in range(1,ma+1):
                            for w in range(v+1,ma+1):
                                fil.write("-%s -%s 0\n"%(self.toDecimal((ma*a+u),(ma*b+v),k,row),self.toDecimal((ma*a+u),(ma*b+w),k,row)))
                            for w in range(u+1,ma+1):
                                for t in range(1,ma+1):
                                    fil.write("-%s -%s 0\n"%(self.toDecimal((ma*a+u),(ma*b+v),k,row),self.toDecimal((ma*a+w),(ma*b+t),k,row)))
        return 0

    def constraint05(self,output,row,fil):
        for i in range(1,row+1):
            for j in range(1,row+1):
                for k in range(1,row):
                    for t in range(k+1,row+1):
                        fil.write("-%s -%s 0\n"%(self.toDecimal(i,j,k,row),self.toDecimal(i,j,t,row)))
        return 0

    def constraint06(self,output,row,fil):
        for i in range(1,row+1):
            for j in range(1,row+1):
                temp=""
                appear=True
                for k in range(1,row+1):
                    if appear==False:
                        temp+=""
                    appear=False
                    temp+="%s "%(self.toDecimal(k,i,j,row))
                fil.write("%s0\n"%temp)
        return 0

    def constraint07(self,output,row,fil):
        for i in range(1,row+1):
            for j in range(1,row+1):
                temp=""
                appear=True
                for k in range(1,row+1):
                    if appear==False:
                        temp+=""
                    appear=False
                    temp+="%s " %(self.toDecimal(i,k,j,row))
                fil.write("%s0\n"%temp)
        return 0

    def constraint08(self,output,row,fil):
        ma=int(math.sqrt(row))
        for i in range(1,row+1):
            for j in range(0,ma):
                for k in range(0,ma):
                    temp = ""
                    first=True
                    for l in range(1,ma+1):
                        for t in range(1,ma+1):
                            if first == False:
                                temp += ""
                            first = False
                            temp += "%s " %(self.toDecimal(ma*j+l,ma*k+t,i,row))
                    fil.write("%s%s\n" %(temp,0))
        return 0

    def CNF_processing(self,output,row,fil,magic):
        cnt=0
        var_num=row*row*row
        if magic==False:
            clause_num=(row*row*int(math.sqrt(row))*int(math.factorial(row)/math.factorial(2)/math.factorial(row-2)))+(row*row)
        elif magic==True:
            clause_num= (row*row*(int(math.sqrt(row))+1)*int(math.factorial(row)/math.factorial(2)/math.factorial(row-2))) + (row*row*(int(math.sqrt(row))+1))
        fil.write("p cnf %s %s\n"%(var_num, clause_num))
#=====================Mininal Encoding=================================
        #each element contains at least oen number
        self.constraint01(output,row,fil)
        #each row contains at most one of each number
        self.constraint02(output,row,fil)
        #each column contains at most one of each number
        self.constraint03(output,row,fil)
        #each number apperas ar most once per grid
        self.constraint04(output,row,fil)
#======================Extened Encoding================================
        if magic==True:
            #at most one number in each entry
            self.constraint05(output,row,fil)
            #Each number appears at least once in each row
            self.constraint06(output,row,fil)
            #Each number appears at least once in each column
            self.constraint07(output,row,fil)
            #Each number appears at least once in each sub-grid
            self.constraint08(output,row,fil)
        fil.close()
        return 0

    def magic_input(self,x):
        dict=[]
        temp=[]
        grid=[]
        for i in range(1,int(math.sqrt(len(x)))+1):
            dict.append(str(i))
        for i in x:
            if i not in dict:
                temp.append('0')
            else:
                temp.append(i)
        new_x="".join(temp)
        #print(new_x)
        for i in range(0, len(x), int(math.sqrt(len(x)))):
            grid.append(new_x[i:i+int(math.sqrt(len(x)))])
        return grid

    def main(self):
        '1.1 detect arguemnts'
        input = ""
        folder = ""
        try:
            file = open(self.puzzle)
            input = file.read()
            file.close()
        except:
            sys.exit("Not A TEXT File")
#=======================
        if self.puzzle=="p096_sudoku.txt":
            res="Grid_CNF"
            dir=os.getcwd() + "/" + res
            if not os.path.exists(dir):
                print("Making a new folder ...")
                os.makedirs(res)
            else:
                print("Folder exists. Overwrite")
                shutil.rmtree(dir)
                os.makedirs(res)
        elif self.puzzle=="top95.txt":
            res="Grid_CNF_extended"
            dir=os.getcwd() + "/" + res
            if not os.path.exists(dir):
                print("Making a new folder ...")
                os.makedirs(res)
            else:
                print("Folder exists. Overwrite")
                shutil.rmtree(dir)
                os.makedirs(res)
        elif self.puzzle=="4x4grid.txt":
            res="Grid_CNF4x4"
            dir=os.getcwd() + "/" + res
            if not os.path.exists(dir):
                print("Making a new folder ...")
                os.makedirs(res)
            else:
                print("Folder exists. Overwrite")
                shutil.rmtree(dir)
                os.makedirs(res)

        '1.2 file process (line by line)'
        title="Grid_CNF"
        output = []
        magic=True
        num=0
        row=0
        #detect if we are in the same 'Grid' still
        check=1
        cnt=0
        first_grid=True
        time_set1=[]
        strn=time.clock()
        for i in input.splitlines():
            if i.startswith("Grid"):
                cnt+=1
                os.chdir(dir)
                magic=False
                if first_grid is False:
                    self.CNF_processing(output,row,fil,magic)
                    tot1=time.clock()-strn
                    time_set1.append(tot1)
                    print("Time CNF-ing file "+fil_name+": %f"%tot1)
                    strn=0
                    strn=time.clock()
                    #reset
                    output=[]
                first_grid=False
                num += 1
                fil_name = title + str(num).zfill(2) + ".txt"
                if check != num:
                    row = 0
                    check += 1
                    fil.close()
                fil=open(fil_name,"w")

            elif not i.startswith("Grid") and magic==False:
                #it you open and write a file repeatedly, the content will be over-written
                i=i.replace('.','0').replace('*','0').replace('?','0')
                row += 1
                output.append(i)
            elif magic==True and i!='\x1a':
                os.chdir(dir)
                #make lines readable
                row+=1
                cnt+=1
                output=self.magic_input(i)
                fil_name = title+str(row).zfill(2)+"_hard.txt"
                fil=open(fil_name,"w")
                col=len(output)
                self.CNF_processing(output,col,fil,magic)
                tot1=time.clock()-strn
                time_set1.append(tot1)
                #reset
                strn=0
                strn=time.clock()

        if output!=[] and magic is False:
            self.CNF_processing(output,row,fil,magic)
            tot1=time.clock()-strn
            time_set1.append(tot1)
            print("Time CNF-ing file "+fil_name+": %f"%tot1)
            #print(time_set)
        print("Total time of converting puzzles into CNF-form files: %f"%(sum(time_set1)))
        print("number of puzzles: %s"%(cnt))
        print("Average time of making each puzzles into CNF-form: %f"%(sum(time_set1)/cnt))
        fil.close()

        '1.3(optional), execute minisat and convert them into another folder'
        strn=0
        cnt=0
        grid_cnf=sorted(os.listdir(os.getcwd()))
        time_set2=[]
        #make another folder to contain the output from minisat
        print("minimal SAT processing ... ...")
        for cnf_txt in grid_cnf:
            strn=0
            cnt+=1
            strn=time.clock()
            resq=""
            try:
                newname="Satenc_"+cnf_txt
                if magic==False:
                    resq=res
                    if res=="Grid_CNF":
                        newfile="../miniSAT_encoded"
                    elif res=="Grid_CNF4x4":
                        newfile="../miniSAT_encoded4x4"
                elif magic==True:
                    resq=res
                    newfile="../miniSAT_hard_encoded"
                if not os.path.exists(newfile):
                    os.makedirs(newfile)
                os.chdir(newfile)
                command="minisat "+"../"+resq+"/"+cnf_txt+" "+newname
                os.system(command)
                tot2=time.clock()-strn
                print("miniSAT encoding for file "+cnf_txt+": %f"%tot2)
                time_set2.append(tot2)
            except:
                sys.exit(cnf_txt+" NOT FOUND")
        print("Total time of encoding CNF-form by miniSAT: %f"%(sum(time_set2)))
        print("number of puzzles: %s"%(cnt))
        print("Average time of encoding each CNF-form by miniSAT: %f"%(sum(time_set2)/cnt))
        tot_time=[]
        for i in range(cnt):
            tot_time.append(time_set1[i]+time_set2[i])
            print("Total time of making puzzle input %s into miniSAT encoded form: %f"%(i+1,tot_time[-1]))
        return tot_time
