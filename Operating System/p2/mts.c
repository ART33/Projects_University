#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <pthread.h>
#include <sys/time.h>

#define SLEEP_TIME 100000.0
#define MAX_NUM 100

typedef struct trains {
	int id;
	char *direction;
	int priority;
	float loadingTime;
	float crossingTime;
	pthread_cond_t conv;
} trains;

struct trains* readyQueue[MAX_NUM];
int useTrack = 0;
int trainsNum;
char *lastTrain;
int crossedNum = 0;
pthread_mutex_t mutex;
pthread_mutex_t track;
pthread_cond_t *condArray;
double getTime;

int compares(trains* t1, trains* t2) {
	int a = 0;
	if (t1->priority > t2->priority) 
	{
		a = 1;
	} 
	else if (t1->priority < t2->priority) 
	{
		a = 0;
	}
	else if (t1->direction == t2->direction)
	{
		if(t1->loadingTime < t2->loadingTime)
		{
			a = 1;
		}
		else if(t1->loadingTime > t2->loadingTime)
		{
			a = 0;
		}
		else 
		{
			if(t1->id < t2->id)
			{
				a = 1;
			}
			else
			{
				a = 0;
			}
		}
	}
	else
	{
		if(crossedNum == 0) {
			if(t1->direction == "East") 
			{
				a = 1;
			}
			else
			{
				a = 0;
			}
		}
		else
		{
			if(t1->direction != lastTrain) 
			{
				a = 1;
			}
			else
			{
				a = 0;
			}
		}
	}
	return a;
}


void sortQueue() 
{
	int i = trainsNum;
	int op = 1;
	int end = 0;
	if(useTrack) {
		end = 1;
	} else {
		end = 0;
	}
	while (op == 1 && i > end) {
		if(compares(readyQueue[i],readyQueue[i-1])) {
			trains* tmp = readyQueue[i];
			readyQueue[i] = readyQueue[i-1];
			readyQueue[i-1] = tmp;
		} else {
			op = 0;
		}
	}
}

void insertQ(trains* t)
{
	readyQueue[trainsNum] = t;
	trainsNum++;
	sortQueue();
}

void removeQ()
{
	int i = 0;
	for(i = 0; i < trainsNum - 1; i++){
		readyQueue[i] = readyQueue[i+1];
	}
	trainsNum--;
}

void *routine(void *args)
{
	trains* t0 = (trains*)args;
    usleep(t0->loadingTime * SLEEP_TIME);
    pthread_mutex_lock(&mutex); 
    insertQ(t0);
    printf("00:00:0%.1f Train %2d is ready to go %4s\n", (t0->loadingTime / 10.0), t0->id - 1, t0->direction);
    pthread_mutex_unlock(&mutex);

    pthread_mutex_lock(&track);
    condArray[t0->id] = t0->conv;
    pthread_cond_wait(&condArray[t0->id], &track);
    if (getTime == 0.0)
    { 
		getTime = (readyQueue[t0->id]->loadingTime / 10.0);
    }
    printf("00:00:0%.1f Train %2d is ON the main track going %4s\n", getTime, t0->id, t0->direction);
    useTrack = 1;
	lastTrain = t0->direction;
	usleep(t0->crossingTime*100000);
	getTime = getTime + (readyQueue[t0->id]->crossingTime / 10.0);
	printf("00:00:0%.1f Train %2d is OFF the main track after going %4s\n", getTime, t0->id, t0->direction);
	crossedNum++;

	pthread_mutex_lock(&mutex);
	pthread_cond_broadcast(&condArray[t0->id]);
	removeQ();
	useTrack = 0;
	pthread_mutex_unlock(&mutex);

	pthread_mutex_unlock(&track); 
	pthread_exit(NULL);
}

int main(int argc, char *argv[])
{
	int linesNum = 0;
	char lines[MAX_NUM][4];
	FILE *fp = fopen(argv[1],"r");
	
	if(fp == 0) 
	{
		perror("Read input file fails");	
	}
	if(fp !=NULL)
	{
		int j = 0;
		while(fgets(lines[j], MAX_NUM, fp))
		{
			j++;		
		}
	}
	trainsNum = 0;
	
	size_t size = strlen(*lines);
	trainsNum = size;
	fclose(fp);


	pthread_t threads[trainsNum];

	pthread_mutex_init(&mutex, NULL);
	pthread_mutex_init(&track, NULL);
	int pid;
	int i;
	for(i = 0; i < trainsNum; i++)
	{
		readyQueue[i]->id = i;
		if(lines[i][0] == 'W') 
		{
			readyQueue[i]->direction = "West";
			readyQueue[i]->priority = 1;
		}
		else if(lines[i][0] == 'w')
		{
			readyQueue[i]->direction = "West";
			readyQueue[i]->priority = 0;
		}
		else if(lines[i][0] == 'E')
		{
			readyQueue[i]->direction = "East";
			readyQueue[i]->priority = 1;
		}
		else if(lines[i][0] == 'e')
		{
			readyQueue[i]->direction = "East";
			readyQueue[i]->priority = 0;
		}
		readyQueue[i]->loadingTime = lines[i][1];
		readyQueue[i]->crossingTime = lines[i][2];
		pthread_cond_init(&readyQueue[i]->conv, NULL);
		pid = pthread_create(&threads[i], NULL, &routine, (void*)&readyQueue[i]);
		if(pid)
		{
			printf("Create thread fails");
			exit(-1);
		}
	}
	for(i = 0; i < trainsNum; i++)
	{
		if(pthread_join(threads[i], NULL) != 0) 
		{
			printf("Join thread fails");
			exit(1);
		}
	}

	pthread_mutex_destroy(&mutex);
	if(trainsNum == crossedNum)
	{
		pthread_mutex_destroy(&track);
		exit(-1);
	}
	for(i = 0; i < trainsNum; i++)
	{
		pthread_cond_destroy(&readyQueue[i]->conv);
	}
	pthread_exit(NULL);
	
	return 0;


}
	
