#!/bin/bash

./network.sh down
./network.sh up createChannel
./network.sh deployCC -l java