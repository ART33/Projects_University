#include <stdio.h>
#include <stdlib.h> 
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

typedef struct directory
{
	int status;
	int blocksNum;
	int fileSize;
	int yearModified;
	int monthModified;
	int dayModified;
	int hourModified;
	int minModified;
	int secModified;
	char *fileName;
} directory;

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

void setDirectory(directory dir, SuperBlock suBl, FILE *fp)
{
	int blockIndex;
	int dirIndex;

	dir.fileName = (char *)malloc(sizeof(char) * 31);

	fseek(fp, suBl.startBlockRD * suBl.blockSize, SEEK_SET);	
	char oneBlock[suBl.blockSize];

	for(blockIndex = 0; blockIndex < suBl.blockCountRD; blockIndex++){
		memset(oneBlock,0,sizeof(oneBlock));			
		fread(oneBlock, sizeof(oneBlock), 1, fp);		
		char *block = oneBlock;
		for(dirIndex = 0; dirIndex < 8; dirIndex++){	
        	dir.status = getVal(block, 0, 1);

        	if(dir.status == 3){	
        		printf("F ");
        	} else if (dir.status == 5){	
        		printf("D ");
        	} else {        		
        		block = block + 64;	
        		continue;
        	}

        	dir.yearModified = 0;
        	dir.fileSize = 0;

			dir.blocksNum = getVal(block, 5, 4);
        	memcpy(&dir.fileSize, block + 9, 4);
			dir.fileSize = ntohl(dir.fileSize);
			memcpy(&dir.yearModified, block + 20, 2);
        	dir.yearModified = ntohs(dir.yearModified);
			dir.monthModified = getVal(block, 22, 1);
			dir.dayModified = getVal(block, 23, 1);
			dir.hourModified = getVal(block, 24, 1);
			dir.minModified = getVal(block, 25, 1);
			dir.secModified = getVal(block, 26, 1);
  	 		memcpy(dir.fileName, block + 27, 31);
			int a = dir.fileSize;
			char *b = dir.fileName;
			int c = dir.yearModified;
			int d = dir.monthModified;
			int e = dir.dayModified;
			int f = dir.hourModified;
			int g = dir.minModified;
			int h = dir.secModified;
			printf("%10d %30s %04d/%02d/%02d %02d:%02d:%02d\n", a, b, c, d, e, f, g, h);
			block = block + 64;
		}
	}

	free(dir.fileName);
	
}



int main(int argc, char **argv){
	FILE *fp;
	SuperBlock superBlock;
	directory dir;

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
	setDirectory(dir, superBlock, fp);
	fclose(fp);
	return 0;
}