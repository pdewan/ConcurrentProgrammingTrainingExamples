import sys
import jarray
from time import time
import csv
from datetime import datetime
from random import randint
import os
from omp import *


def b_sort(arr):
    n_ = len(arr)
    for i in range(n_):
        swapped = False
        for j in range(n_ - 1, i, -1):
            if arr[j - 1] > arr[j]:
                arr[j - 1], arr[j] = arr[j], arr[j - 1]
                swapped = True
        if not swapped:
            break

def merge(arr1, arr2):
    size1, size2 = len(arr1), len(arr2)
    idx_arr1, idx_arr2 = 0, 0
    result = jarray.array([], int)

    while idx_arr1 != size1 or idx_arr2 != size2:
        while idx_arr1 != size1 and (idx_arr2 == size2 or arr1[idx_arr1] <= arr2[idx_arr2]):
            result.append(arr1[idx_arr1])
            idx_arr1 += 1
        while idx_arr2 != size2 and (idx_arr1 == size1 or arr2[idx_arr2] <= arr1[idx_arr1]):
            result.append(arr2[idx_arr2])
            idx_arr2+=1
    return result

if __name__ == '__main__':
    debug = True
    omp_threads_only = True
    platform = 'mac'
    benchmark = 'bubble_sort'
    num_runs = 20
    n_range = range(100, 1100, 100)
    thread_list = [1, 2, 4, 8, 12]
    j_home = os.getenv('JYTHON_HOME') if os.getenv('JYTHON_HOME').endswith('/') else os.getenv('JYTHON_HOME') + '/'
    result_dir = j_home + 'preprocessor/benchmark_results/' + platform + ('/omp_threads_only/' if omp_threads_only else '/standard/') + benchmark + '/'

    # create result directory if needed
    if not os.path.exists(result_dir):
        try:
            os.makedirs(os.path.dirname(result_dir))
            open(result_dir + '.keep', 'a').close()
        except OSError as exc:
            raise Exception('Error: Could not create result directory')

    file_name = result_dir + datetime.now().strftime("%Y_%m_%d--%I_%M")
    file_name += '__' + platform + '__' + benchmark + '__runs_' + str(num_runs) + '__.csv'

    n_vals_row = list(n_range)[:]
    n_vals_row.insert(0, None)

    with open(file_name, 'wb') as file:
        writer = csv.writer(file, delimiter=',')
        writer.writerow(n_vals_row)

    _runtime = []

    for num_threads in thread_list:
        row = []
        row.append(num_threads)
        for n in n_range:
            run_results = []
            for run in range(num_runs):

                arr_1 = jarray.array([], int)
                arr_2 = jarray.array([], int)
                serial_time = sys.maxsize
                parallel_time = sys.maxsize

                for i in range(n):
                    arr_1.append(randint(0, 10000))
                arr_2 = arr_1[:]

                if debug or (num_threads == 1 and not omp_threads_only):
                    s_start = time()
                    for i in range(n):
                        swapped = False
                        for j in range(0, n - i - 1):
                            if arr_1[j] > arr_1[j + 1]:
                                arr_1[j], arr_1[j + 1] = arr_1[j + 1], arr_1[j]
                                swapped = True
                        if not swapped:
                            break

                        s_end = time()
                        serial_time = s_end - s_start

                result = jarray.array([], int)

                if debug or num_threads > 1 or omp_threads_only:
                    p_start = time()


                    sub_arrs = [[] for x in range(num_threads)]
                    #omp parallel num_threads(num_threads) shared(sub_arrs)
                        id = omp_get_thread_num()
                        chunk = int(n/num_threads)
                        sub_arr = jarray.array(arr_2[id*chunk:id*chunk+chunk], int) if id < num_threads - 1 else jarray.array(arr_2[id*chunk:n], int)
                        b_sort(sub_arr)
                        sub_arrs[id] = sub_arr
                    for x in range(num_threads):
                        result = merge(result, sub_arrs[x])



                    p_end = time()
                    parallel_time = p_end - p_start

                if debug:
                    if result != arr_1:
                        raise Exception('results don\'t match!')

                if num_threads == 1 and not omp_threads_only:
                    run_results.append(serial_time)
                else:
                    run_results.append(parallel_time)

            avg = 0
            for result_index in range(num_runs):
                avg += run_results[result_index]
            avg = avg / num_runs

            row.append(avg)
            print('finished ', benchmark, ': ', num_threads, ' threads ', ' n = ', n, ' avg time = ', avg)
        _runtime.append(row)

        with open(file_name, 'ab') as file:
            writer = csv.writer(file, delimiter=',')
            writer.writerow(row)

    speedup_row = []
    with open(file_name, 'ab') as file:
        writer = csv.writer(file, delimiter=',')
        writer.writerow([])
        writer.writerow(n_vals_row)
        for i, val in enumerate(thread_list):
            if val == 1:
                continue
            speedup_row.append(val)
            for x in range(1, len(n_range) + 1):
                try:
                    speedup_row.append(_runtime[0][x] / _runtime[i][x])
                except ZeroDivisionError:
                    speedup_row.append(0)
            writer.writerow(speedup_row)
            speedup_row = []

        #efficiency
        efficiency_row = []
        writer.writerow([])
        writer.writerow(n_vals_row)
        for i, val in enumerate(thread_list):
            if val == 1:
                continue
            efficiency_row.append(val)
            for x in range(1, len(n_range) + 1):
                try:
                    efficiency_row.append(_runtime[0][x] / (_runtime[i][x] * val))
                except ZeroDivisionError:
                    efficiency_row.append(0)
            writer.writerow(efficiency_row)
            efficiency_row = []