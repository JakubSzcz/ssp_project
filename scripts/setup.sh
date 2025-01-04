#!/bin/bash
for i in $(seq 5 5 30); do
    fallocate -l ${i}M "./files/${i}MBfile"
done
