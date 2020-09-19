#!/bin/bash

./network.sh down
./network.sh up createChannel -ca
./network.sh deployCC -l java
