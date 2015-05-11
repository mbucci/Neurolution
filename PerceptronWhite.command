#!/bin/bash

for i in `seq 1 10`;
do
	echo $i
	java Neurolution winequality-white.csv 100 p

done

