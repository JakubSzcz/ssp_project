#!/bin/bash
for i in $(seq 50 50 300); do
    fallocate -l ${i}M "./files/${i}MBfile"
done
