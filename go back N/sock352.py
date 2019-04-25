import binascii
import select
import socket as syssock
import struct
import sys
import random
from time import sleep
from threading import Thread, Lock

# these functions are global to the class and
# define the UDP ports all messages are sent
# and received from

TRANSMIT_PORT = 27182
RECEIVE_PORT = 27183
MAX_PACKET_SIZE = 32 * 1024
HEADER_FORMAT = '!BBBBHHLLQQLL'
HEADER_STRUCT = struct.Struct(HEADER_FORMAT)

DATA    = 0x00
SYN     = 0x01
FIN     = 0x02
ACK     = 0x04
RESET   = 0x08
HAS_OPT = 0xA0

def init(UDPportTx,UDPportRx):   # initialize your UDP socket here
    global TRANSMIT_PORT, RECEIVE_PORT
    TRANSMIT_PORT = int(UDPportTx)
    RECEIVE_PORT = int(UDPportRx)

class socket:
    def __init__(self):
        self.sock = syssock.socket(syssock.AF_INET, syssock.SOCK_DGRAM) # The internal UDP socket
        self.target_addr = None
        self.expected_seq_num = 0

    def bind(self, address):
        address = (address[0], RECEIVE_PORT) # Replace the unused port with the receive port
        print "Binding to (%s, %d)" % (address[0], address[1])
        return self.sock.bind(address)

    def connect(self,address):  # fill in your code here
        address = (address[0], TRANSMIT_PORT) # Replace the unused port with the transmit port
        sequence = random.randint(0, 2048)
        print "Attempting to connect to (%s, %d)" % (address[0], address[1])
        self.__send_packet(address, '', flags = SYN, sequence_no = sequence)
        header, _, _= self.__recv_packet()
        if header['flags'] == SYN | ACK and header['ack_no'] == sequence + 1:
            print "Connection success"
            self.target_addr = address
            self.__send_packet(address, '', flags = ACK, ack_no = header['sequence_no'] + 1)
            return
        else:
            raise 'Connection handshake failed'

    def listen(self,backlog):
        # Not supposed to do anything for this part of the project
        pass

    def accept(self):
        header, _, address = self.__recv_packet()
        self.__send_packet(address, '', flags = SYN | ACK, ack_no = header['sequence_no'] + 1)
        print "Accepted connection from (%s, %d)" % (address[0], address[1])
        self.__recv_packet()
        self.target_addr = address
        return (self,address)

    def close(self):   # fill in your code here
        sequence = random.randint(0, 2048)
        print "Attempting to close connection"
        self.__send_packet(self.target_addr, '', flags = FIN, sequence_no = sequence)
        header, _, _ = self.__recv_packet()
        if header['flags'] == FIN:
            self.__send_packet(self.target_addr, '', flags = ACK,
                               ack_no = header['sequence_no']+ 1)
        print "closing socket"
        self.sock.close()
        return

    def send(self,buffer):
        # Convert the buffer into sendable packets
        packets = []
        last_acked = [-1]
        sent = len(buffer)
        while len(buffer) > 0:
            print "Creating packet #%d" % len(packets)
            packets.append(buffer[:MAX_PACKET_SIZE])
            buffer = buffer[MAX_PACKET_SIZE:]

        # The receive thread
        def thread_func():
            while last_acked[0] < len(packets) - 1:
                header, packet, _ = self.__recv_packet()
                ack_no = header['ack_no'] - self.expected_seq_num
                if header['flags'] == ACK and ack_no > last_acked[0]:
                    print 'Ack received for packet %d' % ack_no
                    last_acked[0] = ack_no
        Thread(target = thread_func).start()

        # The sending loop
        amount_sent = 0
        while last_acked[0] < len(packets) - 1:
            # Send all the packets that have yet to be sent
            for index, packet in enumerate(packets):
                if index <= last_acked[0]: continue
                amount_sent += len(packet)
                seq_no = self.expected_seq_num + index
                print 'Sending packet %d' % seq_no
                self.__send_packet(self.target_addr, packet, flags = DATA, sequence_no = seq_no)
            sleep(0.2)

        self.expected_seq_num += len(packets)
        return sent

    def recv(self,nbytes):
        message = ''
        print 'Attempting to read %d bytes' % nbytes
        while len(message) < nbytes:
            header, payload, addr = self.__recv_packet()
            seq_num = header['sequence_no']
            # check if packets are being sent in the right order
            if seq_num == self.expected_seq_num:
                message += payload
                # send an ACK for this packet
                self.__send_packet(self.target_addr, '', flags=ACK, ack_no=self.expected_seq_num)
                print 'Acked %d, have %d bytes buffered' % (seq_num, len(message))
                # expect next packet
                self.expected_seq_num += 1
            else:
                print 'Out of order packet: %d, expected %d' % (seq_num, self.expected_seq_num)
                # reject packet and send ACK for most recently
                self.__send_packet(self.target_addr, '', flags=ACK, ack_no=self.expected_seq_num)
        return message

    def __send_packet(self, addr, payload, **kwargs):
        version = 1
        flags = kwargs['flags']
        opt_ptr = 0
        protocol = 0
        checksum = kwargs.get('checksum', 0)
        source_port = 0
        dest_port = 0
        sequence_no = kwargs.get('sequence_no', 0)
        ack_no = kwargs.get('ack_no', 0)
        window = kwargs.get('window', 0)

        # Calculate header len from the struct definition
        header_len = HEADER_STRUCT.size

        payload_len = len(payload)
        header = HEADER_STRUCT.pack(version, flags, opt_ptr, protocol, header_len, checksum,
            source_port, dest_port, sequence_no, ack_no, window, payload_len)

        message = header + payload

        return self.sock.sendto(message, addr)

    def __recv_packet(self):
        data, addr = self.sock.recvfrom(64 * 1024)
        header_bytes = data[:HEADER_STRUCT.size]
        fields = ['version', 'flags', 'opt_ptr', 'protocol', 'header_len', 'checksum', \
                  'source_port', 'dest_port', 'sequence_no', 'ack_no', 'window', 'payload_len']
        header_values = HEADER_STRUCT.unpack(header_bytes)
        header = dict(zip(fields, header_values))
        start = header['header_len']
        end = start + header['payload_len']
        payload = data[start:end]
        return header, payload, addr
