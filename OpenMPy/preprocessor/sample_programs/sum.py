from time import sleep, time
import random
import jarray


num_threads = 6
n = 10000000
arr = jarray.array([1 for x in xrange(n)], 'i')

t_start = time()
s_sum = 0
for x in range(len(arr)):
    s_sum += arr[x]
t_end = time()
serial_time = t_end - t_start
print('serial time: ', serial_time)
print('sum = ', s_sum, '\n\n')



t_start = time()
p_sum = 0
#pragma omp parallel num_threads(num_threads) reduction(+:p_sum)
    #pragma omp for schedule(static)
        for x in range(len(arr)):
            p_sum += arr[x]
t_end = time()

parallel_time = t_end - t_start

print('parallel time: ', parallel_time)
print('sum = ', p_sum)
print('speedup: ', serial_time/parallel_time)
print('efficiency: ', serial_time/(parallel_time * num_threads))
print(p_sum == s_sum)
