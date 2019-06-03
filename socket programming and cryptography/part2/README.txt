Group 76

Members: Ryan Goldstein, George Zakka

The two new relevant members are self.encrypt and self.box.
- self.encrypt: a boolean if encryption is enabled
- self.box: the box for encryption
There's a new method:
- self.generate_box(): Use the connected address and generate a box
Five methods have modifications:
- __init__ creates the fields for encryption
- accept() and connect() take the encryption flag and call generate_box() if necessary
- manage_recvd_data_packet() decrypts a packet if the HAS_OPT bit is on and the OPTIONS field is 1
- create_data_packets() chunks the data in a more pythonic way and encrypts the packets if necessary
