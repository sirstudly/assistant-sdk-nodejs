#!/bin/bash
google-oauthlib-tool --client-secrets credentials.json --credentials devicecredentials.json --scope https://www.googleapis.com/auth/assistant-sdk-prototype --scope https://www.googleapis.com/auth/gcm --save \
    && rsync -av devicecredentials.json amazon_ec2_2024:grpc/google-assistant-grpc/
