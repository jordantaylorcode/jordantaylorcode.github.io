from .response import Response
from CRUD import password as pw
import os

return_codes = {
    'USER_NO_EXIST': 1,
    'READ_ERROR': 2,
    'VALIDATION_ERROR': 3
}

def user(db, logger, username = ''):

    logger.info('read user: ' + username)
    table = db.Table(os.environ['user_table'])

    if _validate_create_user(username):
        try:
            response = table.get_item(
            Key = {'username': username}
            )
            logger.info(f"read user response: {response}")
            if 'Item' in response:
                return response['Item']
            logger.info(f"read user try error")
            return return_codes['USER_NO_EXIST']
        except Exception as e:
            logger.info(f"read user error: {e}")
            return return_codes['READ_ERROR']
    else:
        logger.info(f"read user validation error")
        return return_codes['VALIDATION_ERROR']


def items(db, logger):

    logger.info('read items')
    table = db.Table(os.environ['item_table'])

    try:
        response = table.scan()
        logger.info(f"items: {response['Items']}")
        items = map(_int_items, response['Items'])
        if 'Items' in response:
            return Response(200, {'items': list(items)}).get_response()
        return []
    except Exception as e:
        logger.info(f"read items error: {e}")
        return Response(500, {'status': 'something went wrong'}).get_response()

def item(db, logger, id = ''):

    logger.info(f'read item: {id}')
    table = db.Table(os.environ['item_table'])

    if _validate_create_item(id):
        try:
            response = table.get_item(
                Key = {'item_id': id}
            )
            logger.info(f"read item response: {response}")
            if 'Item' in response:
                logger.info(f"item exists")
                return True
            return False
        except Exception as e:
            logger.info(f"read item error: {e}")
            return return_codes['READ_ERROR']
    else:
        logger.info(f"read item validation error")
        return return_codes['VALIDATION_ERROR']


def login(db, logger, data):
    logger.info('login user: ' + data['username'])
    table = db.Table(os.environ['user_table'])

    if _validate_login_user(data):
        try:
            response = table.get_item(
                Key = {'username': data['username']}
            )
            user = response['Item'] if 'Item' in response else None
            if user and pw.verify_password(user['salt'], user['password'], data['password'], logger):
                logger.info(f"{data['username']} is verified")
                return Response(200, {'status': 'success', 'login': True}).get_response()
            logger.info("invalid login")
            return Response(400, {'status': 'username or password incorrect', 'login': False}).get_response()
        except Exception as e:
            logger.info(f"login user error: {e}")
            return Response(500, {'status': 'something went wrong', 'login': False}).get_response()
    else:
        logger.info(f"login user validation error")
        return Response(400, {'status': 'request validation error', 'login': False}).get_response()

        
def _validate_create_user(username):
    if username == '':
        return False
    return True

def _validate_create_item(id):
    if id == '':
        return False
    return True

def _validate_login_user(data):
    if set(data.keys()) != {'username', 'password'}:
        return False
    return True

def _int_items(x):
    x['item_id'] = int(x['item_id'])
    x['quantity'] = int(x['quantity'])
    return x

