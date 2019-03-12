#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <errno.h>
#include <string.h>
#include <sys/types.h>

#define RECV_BUFF_SIZE 100
#define SEND_BUFF_SIZE 1024
#define PORT_NO 8080
int main(void)
{


  int sock = socket(PF_INET, SOCK_DGRAM, IPPROTO_UDP);
  struct sockaddr_in sa; 
  char recvBuff[RECV_BUFF_SIZE];
  ssize_t recsize;
  socklen_t fromlen;
  char sendBuff[SEND_BUFF_SIZE];
  int numrv;

  memset(sendBuff, 0, sizeof(sendBuff));

  memset(&sa, 0, sizeof sa);
  sa.sin_family = AF_INET;
  sa.sin_addr.s_addr = htonl(INADDR_ANY);
  sa.sin_port = htons(PORT_NO);
  fromlen = sizeof(sa);

  if (-1 == bind(sock, (struct sockaddr *)&sa, sizeof sa)) {
    perror("error bind failed");
    close(sock);
    return 1;
  }

  for (;;) {
      
      recsize = recvfrom(sock, (void*)recvBuff, sizeof recvBuff, 0, (struct sockaddr*)&sa, &fromlen);
      if (recsize < 0) {
         fprintf(stderr, "%s\n", strerror(errno));
         return 1;
          }
      printf("Requested filename: %.*s\n", (int)recsize, recvBuff);
      
     /* Open the file that we wish to transfer */
      FILE *fp = fopen(recvBuff,"r");
      if(fp==NULL)
        {
            printf("File opern error");
            return 1;
        }

      while(1)
        {
            /* Read file in chunks of 1024 bytes */
            
            int nread = fread(sendBuff,1,SEND_BUFF_SIZE,fp);
            printf("Bytes read %d \n", nread);
            /* If read was success, send data. */
            if(nread > 0)
            {
                printf("Sending the file ...\n");
                int n = sendto(sock,sendBuff, strlen(sendBuff), 0,(struct sockaddr*)&sa, sizeof sa);
 
 		printf("%d\n", nread);
   
                if(n<0)
                     {
                               perror("Problem sendto\n");
                               exit(1);
                           }
            }

            if (nread < SEND_BUFF_SIZE)
            {
                if (feof(fp))
                    printf("End of file\n");
                if (ferror(fp))
                    printf("Error reading the file at server program\n");
                break;
            }


        }


  }



close(sock);
return 0;

}
