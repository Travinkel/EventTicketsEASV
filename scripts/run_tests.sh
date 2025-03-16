#!/bin/bash

echo "Running JUnit tests..."

# Go to project root
cd "$(dirname "$0")"/..

# Run Maven Tests
mvn clear test

# Check exit code
if [ $? -eq 0 ]; then
  echo "✅ All tests passed!"
else
  echo "❌ Some tests failed!"
  exit 1
fi