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
  char str[1024] = "Hello";

  sockfd = socket(AF_INET, SOCK_STREAM, 0);

  address.sin_family = AF_INET;
  address.sin_addr.s_addr = inet_addr("127.0.0.1");
  //address.sin_addr.s_addr = inet_addr("192.168.0.10");
  //address.sin_addr.s_addr = htonl(INADDR_ANY);
  address.sin_port = htons(9734);

  len = sizeof(address);
  result = connect(sockfd, (struct sockaddr *)&address, len);
  if (result == -1)
  {
    perror("Oops: client3");
    exit(1);
  }
  write(sockfd, &str, 1024);
  read(sockfd, &str, 1024);
  printf("Server message: %s\n", str);
  close(sockfd);
  exit(0);
}
