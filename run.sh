#!/bin/bash

echo "Cleaning..."
rm -rf out
mkdir out

echo "Compiling..."
javac -cp "lib/*" -d out $(find src -name "*.java") || exit 1

echo "Running..."
java -cp "lib/*;out" MainServer