from omp import *
import sys
import random
import math
from jarray import array, zeros
        
def fillArray(a):
    ran = random.Random()
    for i in  range(len(a)):
        a[i] = ran.randint(0,10000)

def isPrime(num):
    if num == 2: return True

    for i in range(3, int(math.sqrt(num))):
        if (num % i) == 0:
            return False;
	
    return True;


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

a = zeros(n, int)
fillArray(a)

count = 0
		
#omp parallel for shared(a) num_threads(numThreads) reduction(+:count)
    for i in range(len(a)):
        if isPrime(a[i]):
            count += 1

print("Count is ", count)
	



