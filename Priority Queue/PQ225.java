/*
* Jiaquan Zhang
* V00836047
*/

import java.util.Random;
import java.util.Scanner;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class PQ225 {

	private static final int INITIAL_SIZE=1;

	private int[] heapArray;
	private int[] integerArray;
	private int   count;
	private int   size;
	private int comp;
	
	public PQ225() {
		heapArray = new int[INITIAL_SIZE];
		integerArray = new int[INITIAL_SIZE];		
		count = 0;
		comp = 0;
	}
	
	private int[] doubleArray(int[] array) {
		int[] tmp = array;
		array = new int[array.length*2];
		for (int i = 0;i<tmp.length;i++) {
			array[i] = tmp[i];
		}
		return array;
	}
	
	private void childToParent(int a) {
		int parent = 0;
		int tmp = 0;
		if(a > 0) {
			if(a % 2 == 1) {
				parent = (a - 1) / 2;
			} else {
				parent = (a - 2) / 2;
			}
			comp++;
			if(heapArray[parent] > heapArray[a]) {
				tmp = heapArray[a];
				heapArray[a] = heapArray[parent];
				heapArray[parent] = tmp;
				childToParent(parent);
			}
			comp++;
		}
		comp++;
	}
	
	private void parentToChild(int a) {
		int left = a * 2 + 1;
		int right = a * 2 + 2;
		int min = 0;
		if (right >= count) {
			if (left >= count) {
				return;
            } else {
                min = left;
            }
            comp++;
        } else {
            if (heapArray[left] <= heapArray[right]) {
                min = left;
            } else {
                        min = right;
            }
            comp++;
        }
        comp++;
        if(heapArray[a] > heapArray[min]) {
        	int tmp = heapArray[min];
        	heapArray[min] = heapArray[a];
        	heapArray[a] = tmp;
        	parentToChild(min);
        }
        comp++;
	}
	
	public void ranGen(int N, int LOW, int HIGH) {
		int k = 0;
		while(k < N) {
			Random random = new Random();
			int s = random.nextInt(HIGH);
			if(s > LOW) {
				if(integerArray.length == size) {
					integerArray = doubleArray(integerArray);
				}
				comp++;
				integerArray[k] = s;
				k++;
				size++;
			}
			comp++;
			comp++;
		}
		comp++;
	}
		
	public int size() {
		return count;
	}
	
	public void insert(int i) {
		if(heapArray.length == count) {
			heapArray = doubleArray(heapArray);
		}
		comp++;
		heapArray[count] = i;
		count++;
		childToParent(count - 1);
	}
	
	public int deleteMin() {
		int a = heapArray[0];
		heapArray[0] = heapArray[count - 1];
		count--;
		if(count > 0) {
			parentToChild(0);
		}
		comp++;
		return a;
	}
	
	public void makeHeap() {
		while(size > 1) {
			if(heapArray.length == count) {
				heapArray = doubleArray(heapArray);
			}
			comp++;
			insert(integerArray[0]);
			integerArray[0] = integerArray[size-1];
			integerArray[size-1] = 0;
			size--;
		}
		comp = comp + size;
		if(size == 1) {
			if(heapArray.length == count) {
				heapArray = doubleArray(heapArray);
			}
			comp++;
			insert(integerArray[0]);
			integerArray[size-1] = 0;
			size--;
		}
		comp++;
	}
	
	public void heapsort() {
		makeHeap();
		while(count > 0) {
			if(integerArray.length == size) {
				integerArray = doubleArray(integerArray);
			}
			comp++;
			integerArray[size] = deleteMin();
			size++;
		}
		comp = comp + count + 1;
	}
	
	public void test() {
		//Scanner user = new Scanner(System.in);
        //System.out.println("RANDOM or FILES?(Please type in with capital letter)");
        //String op = user.nextLine();
       // if(op.equals("FILES")) {
        	try {
        		//Scanner input = new Scanner(System.in);
        		//System.out.print("Enter the file name with extention : ");
        		File file = new File("100.txt");
        		Scanner input = new Scanner(file);
        		int k = 0;
          	  while (input.hasNextInt()) {
          	  	  int num = input.nextInt();
          	  	  if(integerArray.length == size) {
						integerArray = doubleArray(integerArray);
				  }
				  comp++;
				  integerArray[k] = num;
					k++;
					size++;
			  }
			  input.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		/*} else if(op.equals("RANDOM")) {
			Scanner detail = new Scanner(System.in);
			System.out.println("How many numbers do you want?");
			int N = detail.nextInt();
			System.out.println("What's the lower limit of numbers?(Not include)");
			int LOW = detail.nextInt();
			System.out.println("What's the upper limit of numbers?(Not include)");
			int HIGH = detail.nextInt();
			if(LOW > HIGH) {
				System.out.println("Please type in a bigger num:");
				HIGH = detail.nextInt();
			}
			ranGen(N,LOW,HIGH);
		}*/
        heapsort();
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter("pq_test.txt", false));
			writer.append("The size of array is "+size+"\n");
			writer.append("After sort:");
			for(int i = 0;i < size;i++) {
				writer.append(integerArray[i]+" ");
			}
			writer.append("\n");
			writer.append("During sorting, there are "+comp+" comparisons.");
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		PQ225 a = new PQ225();
		a.test();
	}
}