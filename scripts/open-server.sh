#!/bin/bash
cd files
python3 -m http.server 8080 &
python3 -m http.server 8081 &
python3 -m http.server 8082 &
