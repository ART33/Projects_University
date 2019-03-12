#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <string.h>

typedef struct SuperBlock
{
	char fsID[8];
	int blockSize;
	int blocksNumFS;
	int startBlockFAT;
	int blocksNumFAT;
	int startBlockRD;
	int blockCountRD;
} SuperBlock;

int getVal(char *buff, int startByte, int numBytes)
{
	int i = 0;
	int num = 0;

	char *tmp = (char *)malloc(sizeof(char) * numBytes);
    tmp = memcpy(tmp, buff+startByte, numBytes);

	for(i = 0; i < numBytes; i++){
		num += ((int)tmp[i]<<(8*(numBytes - i - 1)));
	}

	free(tmp);
	return num;
}

SuperBlock setSuperBlock(SuperBlock suBl, char *buff)
{
	suBl.blockSize = getVal(buff, 8, 2);
	suBl.blocksNumFS = getVal(buff, 10, 4);
	suBl.startBlockFAT = getVal(buff, 14, 4);
	suBl.blocksNumFAT = getVal(buff, 18, 4);
	suBl.startBlockRD = getVal(buff, 22, 4);
	suBl.blockCountRD = getVal(buff, 26, 4);
	return suBl;
}

void printSuperBlock(SuperBlock suBl)
{
	printf("Super block information:\n");
	printf("Block size: %d\n", suBl.blockSize);
    printf("Block count: %d\n", suBl.blocksNumFS);
    printf("FAT starts: %d\n", suBl.startBlockFAT);
    printf("FAT blocks: %d\n", suBl.blocksNumFAT);
    printf("Root directory start: %d\n", suBl.startBlockRD);
    printf("Root directory blocks: %d\n", suBl.blockCountRD);
}

void printFatInfo(char *buff, FILE *fp, SuperBlock suBl){
	int i;
	int blockIndex;
	int fatIndex;
	int freeBlocks = 0;
	int resBlocks = 0;
	int allocBlocks = 0;
	int start = suBl.startBlockFAT;
	int size = suBl.blockSize;

	fseek(fp, start*size, SEEK_SET);	//set pointer to the beginning of where the FAT blocks start
	char oneBlock[size];		//data for one block

	for(blockIndex = 0; blockIndex < suBl.blocksNumFAT; blockIndex++){
		memset(oneBlock,0,sizeof(oneBlock));			//set block of data to 0
		fread(oneBlock, sizeof(oneBlock), 1, fp);		//read one block 
		char *block = oneBlock;

		for(fatIndex = 0; fatIndex < (size/4); fatIndex++){

			char *tmp = (char *)malloc(sizeof(char) * 4);

			int val = 0;
			tmp = memcpy(tmp, block, 4);

			for(i=0; i < 4; i++){
                val += ((int)tmp[i]<<(8*(4 - i - 1)));
        	}

			block += 4;
        	free(tmp);

			if(val == 0){
				freeBlocks++;
			} else if(val == 1){
				resBlocks++;
			} else {
				allocBlocks++;
			}
		}
	}

	printf("\nFAT information:\n");
	printf("Free Blocks:%d \n", freeBlocks);
	printf("Reserved Blocks:%d \n", resBlocks);
	printf("Allocated Blocks:%d \n", allocBlocks);

}


int main(int argc, char **argv)
{
	FILE *fp;
	SuperBlock superBlock;

	if(argc < 2){
    	printf("Input Incorrect\n");
    	exit(EXIT_FAILURE);
  	}

  	fp = fopen(argv[1], "rb");

	if(fp == NULL){
		fprintf(stderr,"Read disk image fails\n");
		return -1;
	}

	char *buff = (char *)malloc(sizeof(superBlock));
	fread(buff,sizeof(superBlock),1,fp);
	superBlock = setSuperBlock(superBlock, buff);
	printSuperBlock(superBlock);
	printFatInfo(buff, fp, superBlock);
	fclose(fp);
	return 0;
}