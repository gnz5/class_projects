Group 76

Members: Ryan Goldstein, George Zakka

Maximum payload size has been restricted to 4096, as per the project instructions.

There are no new relevant methods.

- Receiver Window Size -
To be consistent with the provided solution's per-packet seq_no and ack_no, the window size is measured in packets. Each packet is 4096 bytes, so the window size is 32kb / 4096b (8 packets).
There is a new constant, MAX_WINDOW, which determines the number of packets in the window size.
The new member current_window_size stores the sender's current knowledge of the available space on the receiver. It will not send more than that many packets at once.

The receiver now sends ACKs that include the window size.

- Congestion Window
The new member, congestion_control, handles the current size of the congestion window size on the sender. It doubles every successful ACK and returns to 2 on every timeout.

