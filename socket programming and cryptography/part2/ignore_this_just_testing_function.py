import binascii
import threading
import socket as syssock
import struct
import sys
import time
from random import randint
import nacl.utils
import nacl.secret
import nacl.utils
from nacl.public import PrivateKey, Box
from inspect import currentframe, getframeinfo
global sock352portTx
global sock352portRx
global publicKeysHex
global privateKeysHex
global publicKeys
global privateKeys
global ENCRYPT
publicKeysHex = {}
privateKeysHex = {}
publicKeys = {}
privateKeys = {}

def readKeyChain(filename):
    global publicKeysHex
    global privateKeysHex
    global publicKeys
    global privateKeys

    if (filename):
        try:
            keyfile_fd = open(filename,"r")
            for line in keyfile_fd:
                words = line.split()
                # check if a comment
                # more than 2 words, and the first word does not have a
                # hash, we may have a valid host/key pair in the keychain
                if ( (len(words) >= 4) and (words[0].find("#") == -1)):
                    host = words[1]
                    port = words[2]
                    keyInHex = words[3]
                    if (words[0] == "private"):
                        privateKeysHex[(host,port)] = keyInHex
                        privateKeys[(host,port)] = nacl.public.PrivateKey(keyInHex, nacl.encoding.HexEncoder)
                    elif (words[0] == "public"):
                        publicKeysHex[(host,port)] = keyInHex
                        publicKeys[(host,port)] = nacl.public.PublicKey(keyInHex, nacl.encoding.HexEncoder)
        except Exception,e:
            print ( "error: opening keychain file: %s %s" % (filename,repr(e)))
    else:
            print ("error: No filename presented")

    print "PUBLIC KEY"
    for i in publicKeys:
        print i
        print publicKeys[i]
    print "\nPRIVATE KEY"
    for i in privateKeys:
        print i
        print privateKeys[i]
        try:
            print privateKeys[("localhost", "9999")]
        except KeyError:
            print "no such key"
    return (publicKeys,privateKeys)

readKeyChain("keychain.txt")