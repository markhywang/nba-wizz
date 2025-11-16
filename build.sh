#!/bin/bash

# Prerequisites:
# 1. Install the Google Cloud SDK: https://cloud.google.com/sdk/docs/install
# 2. Authenticate with Google Cloud by running the following command in your terminal once:
#    gcloud auth application-default login
# 3. Make sure you have a .env file in the project root with your GEMINI_API_KEY.

# Set your Gemini API key here
export GEMINI_API_KEY='YOUR_API_KEY_HERE'

# Run the application
mvn package
mvn exec:java

# Test the application
mvn test