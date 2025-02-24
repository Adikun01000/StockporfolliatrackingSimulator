#!/bin/bash

# Create bin directory if it doesn't exist
mkdir -p bin

# Compile all Java files
javac -d bin src/*.java

# Run the application
java -cp bin Main
