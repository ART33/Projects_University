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

typedef struct directory
{
	int status;
	int startBlock;
	int blocksNum;
	int fileSize;
	int createdTime;
	int modifiedTime;
	char *fileName;
	int unused;
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

int findFile(FILE *fp, SuperBlock suBl, char *orFile, int fileSize){
	int blockIndex;
	int dirIndex;
	directory dir;
	dir.startBlock = 0;
	fileSize = 0;


	fseek(fp, suBl.startBlockRD * suBl.blockSize, SEEK_SET);	
	char oneBlock[suBl.blockSize];

	for(blockIndex = 0; blockIndex < suBl.blockCountRD; blockIndex++){
		memset(oneBlock,0,sizeof(oneBlock));			
		fread(oneBlock, sizeof(oneBlock), 1, fp);		
		char *block = oneBlock;

		for(dirIndex = 0; dirIndex < 8; dirIndex++){	
			dir.status = getVal(block, 0, 1);

        	if(dir.status == 3){
        		dir.fileName = (char *)malloc(sizeof(char) * 31);

        		memcpy(dir.fileName, block + 27, 31);

        		if(strcmp(orFile, dir.fileName) == 0){
        			memcpy(&dir.startBlock, block + 5, 4);
        			dir.startBlock = ntohl(dir.startBlock);
        			memcpy(&fileSize, block + 9, 4);
        			fileSize = ntohl(fileSize);
        			free(dir.fileName);
        			return dir.startBlock;
				}
        	}
			block = block + 64;	
		}
	}
	free(dir.fileName);
	return -1;
}


int copyFile(SuperBlock suBl, FILE *fp, char *fileName, char *copiedFile){
	int fileSize;
	int startBlock;
	fileSize = 0;
	startBlock = 0;
	startBlock = findFile(fp, suBl, fileName, fileSize);

	if(startBlock == -1){
		printf("File not Found \n");
		return startBlock;
	} 

	FILE *fpNew = fopen(copiedFile, "wb");
	suBl.blockSize = ntohs(suBl.blockSize);

	int copiedBytes = 0;
	
	while(copiedBytes < fileSize){
		char block[suBl.blockSize];
		int copySize = sizeof(block);
		if((fileSize - copiedBytes) < suBl.blockSize){	
			copySize = fileSize - copiedBytes;
		}
		fseek(fp, startBlock * suBl.blockSize, SEEK_SET);	
		fread(block, copySize, 1, fp);					
		fwrite(block, copySize, 1, fpNew);				

		copiedBytes = copiedBytes + copySize;

		int newStart = 0;
		suBl.startBlockFAT = ntohl(suBl.startBlockFAT);

		fseek(fp, (suBl.startBlockFAT * suBl.blockSize) + (startBlock * 4), SEEK_SET);		//move to beginnning of FAT plus start block

		char tmp[4];
		memset(tmp,0,sizeof(tmp));		
		fread(tmp,sizeof(tmp),1,fp);	
		memcpy(&newStart, tmp, sizeof(tmp));	
		newStart = ntohl(newStart);

		startBlock = newStart;
		if(startBlock == 0xFFFFFFFF){		
			break;
		}
	}

	printf("File Copied\n");
	fclose(fpNew);
	return startBlock;
}

int main(int argc, char **argv)
{
	FILE *fp;
	SuperBlock superBlock;

	if(argc != 4){
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

	copyFile(superBlock, fp, argv[2], argv[3]);
	

	fclose(fp);
	return 0;}