Group 76
Members: Ryan Goldstein, George Zakka

Tested on server2 and client2

Design choices:
- The setup is a 3-way handshake (SYN, SYN/ACK, ACK)
- The teardown is a 2-way double handshake
- There's no need to track timeouts for each individual packet, because the entire 'send' is sent every attempt
- There is fairly comprehensive logging that indicates the current progress state
