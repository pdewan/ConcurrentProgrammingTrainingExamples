from time import sleep, time
import random
import jarray
from omp import *

num_threads = 2

#omp parallel num_threads(num_threads)
    #omp critical
        print("Hello from thread: " + str(omp_get_thread_num()))