#!/bin/bash
set -e

S3_BUCKET="www.harism.dev"
CLOUDFRONT_DIST_ID="EJVRKI7S5GR3J"
PROD_DIR="./prod"
AWS_PROFILE="harism-deploy"

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

if ! command -v aws &> /dev/null; then
    echo -e "${RED}Error: AWS CLI is not installed${NC}"
    echo "Install it with: mise use -g awscli or brew install awscli"
    exit 1
fi

echo -e "${YELLOW}Starting deployment...${NC}"
echo "S3 Bucket: $S3_BUCKET"
echo "CloudFront Distribution: $CLOUDFRONT_DIST_ID"
echo ""

echo -e "${YELLOW}Uploading prod directory to S3...${NC}"
aws s3 sync "$PROD_DIR" "s3://$S3_BUCKET" \
    --profile "$AWS_PROFILE" \
    --delete \
    --cache-control "max-age=31536000" \
    --exclude "index.html" \
    --exclude "*.json"

echo -e "${YELLOW}Uploading index.html with no-cache...${NC}"
aws s3 cp "$PROD_DIR/index.html" "s3://$S3_BUCKET/index.html" \
    --profile "$AWS_PROFILE" \
    --cache-control "no-cache, no-store, must-revalidate"

echo -e "${YELLOW}Creating CloudFront invalidation...${NC}"
INVALIDATION_ID=$(aws cloudfront create-invalidation \
    --profile "$AWS_PROFILE" \
    --distribution-id "$CLOUDFRONT_DIST_ID" \
    --paths "/*" \
    --query 'Invalidation.Id' \
    --output text)

echo -e "${GREEN}Invalidation created: $INVALIDATION_ID${NC}"
echo ""

echo -e "${YELLOW}Waiting for invalidation to complete...${NC}"
aws cloudfront wait invalidation-completed \
    --profile "$AWS_PROFILE" \
    --distribution-id "$CLOUDFRONT_DIST_ID" \
    --id "$INVALIDATION_ID"

echo -e "${GREEN}Deployment completed successfully!${NC}"
echo "Files synced to S3 and CloudFront cache invalidated."
