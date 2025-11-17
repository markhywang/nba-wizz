#!/bin/bash

# Set your Gemini API key here
$env:GEMINI_API_KEY='YOUR_API_KEY_HERE'

# Run the application
mvn package
mvn compile exec:java

# Test the application
mvn test