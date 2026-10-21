#!/bin/bash
set -euo pipefail

# Dummy AWS Variables 👇
#   DynamoDB Local ignores these, but the AWS CLI requires some value to be present.
#   Hardcoded, not inherited from the caller's shell.
export AWS_ACCESS_KEY_ID="local"
export AWS_SECRET_ACCESS_KEY="local"
export AWS_PAGER="" # Disable the CLI's default pager, prints the output directly.

# DB Variables 👇
#   Same names and defaults as the API (see config.clj).
export AWS_DEFAULT_REGION="${DB_REGION:-us-east-1}"
DYNAMODB_ENDPOINT="http://${DB_HOSTNAME:-localhost}:${DB_PORT:-8000}"
TABLE_NAME="springfield-incidents"

if aws dynamodb describe-table \
    --endpoint-url "$DYNAMODB_ENDPOINT" \
    --table-name "$TABLE_NAME" >/dev/null 2>&1; then
  echo -e "\033[1;32mTable $TABLE_NAME already exists!\033[0m"
  echo -e "\033[1;32mAGORA É SÓ CORRER PRO ABRAÇO   🍻\033[0m"
  exit 0
fi

aws dynamodb create-table \
  --endpoint-url "$DYNAMODB_ENDPOINT" \
  --table-name "$TABLE_NAME" \
  --attribute-definitions \
      AttributeName=id,AttributeType=S \
      AttributeName=source,AttributeType=S \
  --key-schema AttributeName=id,KeyType=HASH \
  --global-secondary-indexes \
      '[{
        "IndexName": "source-index",
        "KeySchema": [{"AttributeName": "source", "KeyType": "HASH"}],
        "Projection": {"ProjectionType": "ALL"}
      }]' \
  --billing-mode PAY_PER_REQUEST

echo -e "\n\033[1;32mTable $TABLE_NAME created 🎉\033[0m\n"

aws dynamodb put-item \
  --endpoint-url "$DYNAMODB_ENDPOINT" \
  --table-name "$TABLE_NAME" \
  --item '{
    "id":          {"S": "'"$(uuidgen)"'"},
    "reporter":    {"S": "Homer Simpson"},
    "source":      {"S": "springfield-nuclear"},
    "severity":    {"S": "critical"},
    "description": {"S": "Donut stuck in reactor panel"},
    "status":      {"S": "solved"},
    "end_reason":  {"S": "Donut removed from reactor panel"},
    "reported_at": {"S": "2024-01-03T08:00:00Z"},
    "ended_at":    {"S": "2024-01-03T08:05:00Z"}
  }'

aws dynamodb scan \
  --endpoint-url "$DYNAMODB_ENDPOINT" \
  --table-name "$TABLE_NAME" \
  --output table

echo -e "\n\033[1;32mSeed incident added 🍩\033[0m\n"
