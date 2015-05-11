#!/bin/bash

for i in `seq 1 10`;
do
	echo $i
	java Neurolution winequality-red.csv 100 l

done

