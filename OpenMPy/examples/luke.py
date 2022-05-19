from omp import *
import sys
import random
import math
from jarray import array, zeros
        
def fillArray(a):
    ran = random.Random()
    for i in  range(len(a)):
        a[i] = ran.uniform(0,100)

numThreads = 0
n = 0

if len(sys.argv) != 3:
    print("usage: ", sys.argv[0], " <num_threads> <num_items>")
    sys.exit(1)
    
try:
    numThreads = int(sys.argv[1])
    n = int(sys.argv[2])
except ValueError:
    print("Cannot convert arguments to integers")
    sys.exit(1)

a = zeros(n, float)
fillArray(a)

sum = 0

#omp parallel for shared(a) num_threads(numThreads) reduction(+:sum)
    for i in range(len(a)):
        sum += a[i]

print("Sum is ", sum)

