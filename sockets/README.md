# sockets

Unix-like example.

## Running the server:

`$ ./local_server`  

*Server waiting*

`$ ls -lF server_socket`

*srwxrwxr-x 1 user user 0 Apr 30 02:44 server_socket=*

`$ ps lx`

*0  1000 18260 11226  20   0   4512   740 wait_w S+   pts/1      0:01 ./local_server*

## Running the client:

`$ ./local_client`  

*Char from server = B*

