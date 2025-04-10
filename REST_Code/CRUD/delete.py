import os
from .response import Response

def item(db, logger, data = {}):
    logger.info(f"delete item: {data}")
    table = db.Table(os.environ['item_table'])

    if 'item_id' in data:
        item_id = data['item_id']

        try:
            response = table.delete_item(
                Key={
                    'item_id': item_id
                },
                ReturnValues='ALL_OLD'
            )
            logger.info(f"delete response: {response}")
            if 'Attributes' not in response:
                return Response(400, {'status': 'item does not exist'}).get_response()
            return Response(201, {'status': 'success'}).get_response()
        except Exception as e:
            logger.info(f"update error: {e}")
            return Response(500, {'status': 'error deleting item'}).get_response()
    else:
        return Response(400, {'status': 'malformed request'}).get_response()