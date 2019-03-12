#include <stdlib.h>
#include <stdio.h>
#include <readline/readline.h>
#include <readline/history.h>
#include <string.h>
#include <sys/wait.h>
#include <sys/types.h>
#include <errno.h>
#include <unistd.h> 

pid_t pid[256];
int count = 0;
char* file[256];

int execmd(char** tokens, int bg)
{
    int status;
    pid_t cpid = fork(); 

    if(cpid < 0)
    {
    	printf("SSI: Fork Failed");
    }
    else if(cpid == 0)
    {//child process
	if(bg == 0)
	{//not background
	    if (execvp(tokens[0], tokens) == -1)
	    { 
	        printf("SSI: no such command\n");
    	    }
	}
	else
	{
	    if (execvp(tokens[1], tokens) == -1)
	    { 
		printf("SSI: no such command\n");
    	    }
	}
    	
	exit(1);
    }
    else
    {//parent process
	if (bg == 0)
	{
	    do 
	    {
       	        waitpid(cpid, &status, WUNTRACED);
	    } while (!WIFEXITED(status) && !WIFSIGNALED(status));
	}
	else
	{
	    char prompt[256] = "SSI: ";
	    char cur[256] = "";
	    getcwd(cur, 256);
	    strcat(prompt, cur);
	    pid[count] = cpid;
	    file[count] = prompt;
	    count++;
	}
    }

    return 1;
}

void cdcmd(char *input)
{
	
    if (input == NULL || (strcmp(input, "~") == 0))
    {//change to root
	    chdir(getenv("HOME"));
    }
    else if(strncmp(input, "..", 2) == 0)
    {//move up
	    chdir("..");
    }
    else
    {
    	if(chdir(input) != 0) 
    	{
    		printf("SSI: no such directory\n");
    	} 
    }
}

void termination()
{
    int status;
    pid_t tmp = waitpid(-1,&status,WNOHANG);
    int i = 0;
    int b = 0;
    if (tmp > 0) 
    {
	while (tmp != pid[i])
	{
	    i++;
    	}
	printf("%d: %d has terminated.\n", pid[i],i+1);
	if(i != count - 1)
	{//delete the terminated process from the list
	    for(b = i; b < count - 1; b++)
	    {
		pid[b] = pid[b+1];
		file[b] = file[b+1];
	    }
	}
	count--;
    }
}

void bglist()
{
    int i;
    for (i = 0; i < count; i++) 
    {
	printf("%d: %s %d",pid[i],file[i],i+1);
    }
    printf("Total Background Jobs: %d\n",i);
}

int main()
{
    
    int bailout = 0;
    int bg = 0;
    while (!bailout) {//loop start here
	char prompt[256] = "SSI: ";
	char cur[256] = "";
	getcwd(cur, 256);
	strcat(prompt, cur);
	strcat(prompt, " > ");//show prompt
    	

	char* reply = readline(prompt);
	termination();
	char** tokens = malloc(64*sizeof(char*));//tokenize the input
	char* tok;

	tok = strtok(reply," ");
	int i = 0;
	while(tok != NULL)
	{
            tokens[i] = tok;
            i++;
	    tok = strtok(NULL," ");
	}
	tokens[i] = NULL;
	
	if (tokens[0] == NULL)
	{//press the enter
	    bailout = 0;
	}
	else if (!strcmp(tokens[0], "cd")) 
	{//change directory
	    cdcmd(tokens[1]);
        } 
	else if (!strcmp(tokens[0], "bg"))
	{//backgroud process
	    execmd(tokens,1);
	}
	else if (!strcmp(tokens[0], "bglist"))
	{//display backgroud list
	    bglist();

	}
	else if (!strcmp(tokens[0], "quit") || !strcmp(tokens[0], "exit"))
	{//exit the shell
	    bailout = 1;
	}
        else
        {//normal execute
	    execmd(tokens,0);
        }
	
        free(reply);
    }
    printf("Shell Exited.\n");
}
	    
