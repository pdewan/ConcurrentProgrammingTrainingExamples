import sys
import jarray
from time import time
from ompy.omp import *


def zero_out_array(arr):
    for i in range(len(arr)):
        arr[i] = 0.0


def print_array(arr, n, label):
    print(label + ':')
    for i in range(n):
        for j in range(n):
            print(arr[i * n + j])
        print('')
    print('')


def matrix_mult(arr_1, arr_2, result_arr, start, end):
    row_sum = 0
    for i in range(start, end):
        for j in range(n):
            for k in range(n):
                row_sum += arr_1[i * n + k] * arr_2[k * n + j]
            result_arr[i * n + j] = row_sum
            row_sum = 0

if __name__ == '__main__':
    # retrieve command line arguments, script must be run with: >jython ParallelMatrixMultiplication.py n num_threads
    n = int(sys.argv[1])
    num_threads = int(sys.argv[2])
    arr_1 = jarray.array([], float)
    arr_2 = jarray.array([], float)
    p_result_arr = jarray.array([], float)
    s_result_arr = jarray.array([], float)

    # initialize first array (matrix)
    for i in range(n*n):
        arr_1.append(float(i))

    # initialize second array (matrix)
    for i in range(n*n):
        arr_2.append(float(i))

    # initialize second array (matrix)
    for i in range(n*n):
        p_result_arr.append(0.0)
        s_result_arr.append(0.0)

    #print_array(arr_1, n, 'array 1')
    #print_array(arr_2, n, 'array 2')
    #print_array(result_arr, n, 'result array')
    # do serial matrix multiplication
    s_start = time()
    matrix_mult(arr_1, arr_2, s_result_arr, 0, n)
    s_end = time()
    serial_time = s_end - s_start

    #print_array(s_result_arr, n, 'result after serial mult')
    print('Serial execution took ', serial_time)

    # do parallel matrix multiplication
    p_start = time()

    #pragma omp parallel num_threads(num_threads)
        chunk = int(n / num_threads)
        id = omp_get_thread_num()
        my_start = id * chunk
        my_end = my_start + chunk if id < num_threads - 1 else n
        matrix_mult(arr_1, arr_2, p_result_arr, my_start, my_end)

    p_end = time()
    parallel_time = p_end - p_start

    #print_array(p_result_arr, n, 'result after parallel mult')
    print('Parallel execution took ', parallel_time)

    print('Speedup = ', serial_time/parallel_time)
    print('Efficiency = ', serial_time/(parallel_time*num_threads))
    print(s_result_arr == p_result_arr)