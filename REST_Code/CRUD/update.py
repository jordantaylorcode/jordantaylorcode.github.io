from .response import Response
from .read import item as read_item, return_codes
import os

def item(db, logger, data={}):
    logger.info(f"update item: {data}")
    table = db.Table(os.environ['item_table'])

    if _validate_item(data):
        #check if item exists
        invalid_read_responses = 1, 2, 3
        item = read_item(db, logger, data['item_id'])
        
        if item == return_codes['READ_ERROR']:
            return Response(500, {'status': 'Problem reading item'}).get_response()
        if item == return_codes['VALIDATION_ERROR']:
            return Response(400, {'status': 'read validation error'}).get_response()
        if item:
            try:
                item_id = data['item_id']
                expression, keys, values = _expression(data)
                response = table.update_item(
                    Key={'item_id': item_id},
                    UpdateExpression=expression,
                    ExpressionAttributeNames=keys,
                    ExpressionAttributeValues=values
                )
                logger.info(f"update item response: {response}")
                return Response(201, {'status': 'success'}).get_response()
            except Exception as e:
                logger.error(f"update item error: {e}")
                return Response(500, {'status': 'Problem updating item'}).get_response()
        else:
            return Response(404, {'status': 'item does not exist'}).get_response()
    else:
        return Response(400, {'status': 'item validation error'}).get_response()


def _validate_item(data):
    if set(data.keys()) != set(['item_id', 'name', 'quantity', 'description', 'category']):
        return False
    return True


def _expression(data):
    """creates an update expression for dynamodb"""
    del data['item_id']
    result = 'SET '
    keys = {}
    values = {}
    counter = 0
    for key, value in data.items():
        keys[f"#{key}"] = key
        #type_value = 'S' if type(value) == str else 'N'
        #values[f":value{counter}"] = {type_value: value}
        values[f":value{counter}"] = value
        result += f"#{key}=:value{counter}, "
        counter += 1
    result = result[:-2]
    return result, keys, values

