from CRUD import password as pw
from .response import Response
from .read import user as read_user, return_codes, item as read_item
import os


def item(name='', quantity=0, description='', category=''):
    return 'created an item'


def user(db, logger, data = {}):

    logger.info(f"create user: {data}")
    table = db.Table(os.environ['user_table'])
    
    if _validate_user(data, logger):
        # check if user exists
        invalid_read_responses = 1, 2, 3
        user = read_user(db, logger, data['username'])

        if user not in invalid_read_responses:
            logger.info(f"create user error: username already exists")
            return Response(400, {'status': 'username already exists'}).get_response()
        if user in invalid_read_responses[1:]:
            logger.info(f"create user error: read user error {user}")
            return Response(500, {'status': 'something went wrong'}).get_response()


        salt, hashed_pw = pw.hash_password(data['password'])
        item = {
            'username': data['username'].lower(),
            'salt': salt,
            'password': hashed_pw
        }
        try:
            # put the item into the database and return successful response
            result = table.put_item(Item=item)
            logger.info(f"create user: {result}")
            return Response(201, {'status': 'success'}).get_response()
        except db.meta.client.exceptions.BucketAlreadyExists as e: # figure out suitable exception
            # database error
            logger.info(f"create user error: {e}")
            return Response(500, {'status': 'something went wrong'}).get_response()
    else:
        return Response(400, {'status': 'invalid user data'}).get_response()


def item(db, logger, data = {}):

    logger.info(f"create item: {data}")
    table = db.Table(os.environ['item_table'])

    if _validate_item(data):
        #check if item exists
        invalid_read_responses = 1, 2, 3
        item = read_item(db, logger, data['item_id'])

        if not item:
            logger.info('proceed with create item')
            input_item = {
                'item_id': data['item_id'],
                'name': data['name'],
                'quantity': data['quantity'],
                'description': data['description'],
                'category': data['category']
            }
            try:
                result = table.put_item(Item=input_item)
                logger.info(f"create item: {result}")
                return Response(201, {'status': 'success'}).get_response()
            except Exeption as e:
                logger.info(f"create item error: {e}")
                return Response(500, {'status': 'something went wrong'}).get_response()
        if item == return_codes['READ_ERROR']:
            logger.info(f"create item error: item read error")
            return Response(400, {'status': 'somrething went wrong'}).get_response()
        if item == return_codes['VALIDATION_ERROR']:
            logger.info(f"create item error: read validation error")
            return Response(500, {'status': 'something went wrong'}).get_response()
        if item:
            logger.info(f"create item error: item already exists")
            return Response(400, {'status': 'item already exists'}).get_response()
    else:
        return Response(400, {'status': 'invalid item data'}).get_response()


def _validate_user(data, logger):
    logger.info(f"validate user: {data}")
    if 'username' not in data or 'password' not in data:
        return False
    if len(data['password']) < 8 or len(data['username']) < 4:
        return False
    # make sure that unneeded data doesn't get added to database
    if set(data.keys()) != set(['username', 'password']):
        return False
    return True


def _validate_item(data):
    if set(data.keys()) != set(['item_id', 'name', 'quantity', 'description', 'category']):
        return False
    return True

