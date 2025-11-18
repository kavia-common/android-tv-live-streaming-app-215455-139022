#!/bin/bash
cd /home/kavia/workspace/code-generation/android-tv-live-streaming-app-215455-139022/androidtv_frontend
./gradlew lint
LINT_EXIT_CODE=$?
if [ $LINT_EXIT_CODE -ne 0 ]; then
   exit 1
fi

