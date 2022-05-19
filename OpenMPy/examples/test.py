from omp import *
import sys
import random

print("before parallel")
numIters = 0

if len(sys.argv) != 2:
    print("usage: ", argv[0], " <num_iterations>")

try:
    numIters = int(sys.argv[1])
except ValueError:
    print("Cannot convert argument to integer")
    sys.exit(1)

numIn = 0
numOut = 0

print("Here")

print("before parallel")
#omp parallel num_threads(4) reduction(+:numIn) reduction(+:numOut)
while True:
    localRandom = random.Random()
    print("in parallel")

    myIters = numIters
    numIn = 0
    numOut = 0
    #omp for
    for i in range(myIters):
        #get random number from 0 to 1
        x = localRandom.uniform(0,1)
        y = localRandom.uniform(0,1)
        hyp = sqrt(x*x + y*y)
        if hyp < 1.0:
            numIn += 1
        else:
            numOut += 1

p = float(numIn)/(numIn+numOut)
fourp = 4*p
print("Pi is ", fourp)
