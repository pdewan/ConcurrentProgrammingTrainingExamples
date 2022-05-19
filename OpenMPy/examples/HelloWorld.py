from omp import *
import sys

threadCount = int(sys.argv[1]) 

#omp parallel num_threads(threadCount)
    myID = omp_get_thread_num()
    tCount = omp_get_num_threads()
    #omp critical
        print("Hello from ", myID, " of ", tCount)
