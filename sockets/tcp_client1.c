#include <sys/types.h>
#include <sys/socket.h>
#include <stdio.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <stdlib.h>
int main()
{
  int sockfd;
  int len;
  struct sockaddr_in address;
  int result;
  char ch = 'A';

  sockfd = socket(AF_INET, SOCK_STREAM, 0);

  address.sin_family = AF_INET;
  // INADDR_ANY = All machine network interfaces
  address.sin_addr.s_addr = htonl(INADDR_ANY);
  //address.sin_addr.s_addr = inet_addr("127.0.0.1");
  //address.sin_addr.s_addr = inet_addr("192.168.0.10");
  address.sin_port = htons(9734);

  len = sizeof(address);
  result = connect(sockfd, (struct sockaddr *)&address, len);
  if (result == -1)
  {
    perror("Oops: client2");
    exit(1);
  }
  write(sockfd, &ch, 1);
  read(sockfd, &ch, 1);
  printf("Char from server = %c\n", ch);
  close(sockfd);
  exit(0);
}
