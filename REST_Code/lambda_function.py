import boto3
import os
import json
import logging
from CRUD import *


# for logging
logger = logging.getLogger()
logger.setLevel("INFO")

# database and credentials
dynamodb = boto3.resource('dynamodb')


def lambda_handler(event, context):
    logger.info(f"event body: {type(event['body'])}")
    if event['body']:
        data = json.loads(event['body'])  
    method = event['httpMethod']
    resource = event['resource']
        

    # request routing
    if method == 'GET' and resource == '/items': # get all items
        logger.info("GET /items")
        response = read.items(dynamodb, logger)
    if method == 'POST' and resource == '/user': # create user
        logger.info("POST /user")
        response = create.user(dynamodb, logger, data = data)
    if method == 'POST' and resource == '/login': #login
        logger.info("POST /login")
        response = read.login(dynamodb, logger, data = data)
    if method == 'POST' and resource == '/item': #create item
        logger.info("POST /item")
        response = create.item(dynamodb, logger, data = data)
    if method == 'PATCH' and resource == '/item': #update item
        logger.info("PATCH /item")
        response = update.item(dynamodb, logger, data = data)
    if method == 'DELETE' and resource == '/item': #delete item
        logger.info("DELETE /item")
        response = delete.item(dynamodb, logger, data = data)

    return response
