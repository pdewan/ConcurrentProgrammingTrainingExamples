#jython program
import sys
sys.path.append('/home/mrogers/src/OpenMPy/preprocessor/ompy')
import sys
from jarray import array, zeros
from runtime import *
from Queue import Queue
from threading import current_thread


_num_threads_ = 1
from omp import *
import sys
threadCount = int(sys.argv[1])
_num_threads_ = threadCount
def _target_0(_manager_):

    myID = omp_get_thread_num()
    tCount = omp_get_num_threads()
    _manager_.critical_lock.acquire()

    print "Hello from ", myID, " of ", tCount
    _manager_.critical_lock.release()
_manager_outer_ = RuntimeManager(_num_threads_)
submit(_target_0, _num_threads_, args=(_manager_outer_,))

