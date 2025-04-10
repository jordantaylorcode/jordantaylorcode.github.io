import json

class Response:
    def __init__(self,  status_code,  message):
        self.status_code = status_code
        self.message = message
        self.headers = {'Content-Type': 'application/json'}
        self.response = self._construct_response()

    def _construct_response(self):
        return {
            'statusCode': self.status_code,
            'headers': self.headers,
            'body': json.dumps(self.message)
        }

    def get_response(self):
        return self.response
        

    