import hashlib
import os
import binascii


def hash_password(pw):
    """create salt and combine with password to create hash
    returns (salt, hashed password + salt)"""
    # get random binary sequence
    salt = os.urandom(16)

    # create salted password binary sequence
    salted_password = salt + pw.encode('utf-8')

    # hash the sequence and convert to string
    hashed_password = hashlib.sha256(salted_password).hexdigest()

    # convert the salt to string and return both salt and hashed password
    salt_hex = binascii.hexlify(salt).decode('utf-8')
    return salt_hex, hashed_password


def verify_password(db_salt, db_password, password, logger):
    """verify password against hash"""
    # generate hash from salt + login password
    salt = binascii.unhexlify(db_salt.encode('utf-8'))
    salted_password = salt + password.encode('utf-8')

    # hash the incomming password with the salt from the database
    comp_password = hashlib.sha256(salted_password).hexdigest()

    # return true or false depending on equality
    logger.info(f"{comp_password} == {db_password}")
    return comp_password == db_password