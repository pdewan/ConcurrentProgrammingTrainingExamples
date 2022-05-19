import sys
import jarray
from time import time
import csv
from datetime import datetime
import os
from omp import *


def matrix_mult(arr_1, arr_2, result_arr, start, end):
    # multiply two square matrices
    row_sum = 0
    for i in range(start, end):
        for j in range(n):
            for k in range(n):
                row_sum += arr_1[i * n + k] * arr_2[k * n + j]
            result_arr[i * n + j] = row_sum
            row_sum = 0

if __name__ == '__main__':
    platform = 'mac'
    benchmark = 'matrix_mult'
    debug = False
    omp_threads_only = True
    num_runs = 5
    n_range = range(10, 250, 20)
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

                arr_1 = jarray.array([], 'f')
                arr_2 = jarray.array([], 'f')
                p_result_arr = jarray.array([], 'f')
                s_result_arr = jarray.array([], 'f')

                serial_time = sys.maxsize
                parallel_time = sys.maxsize

                for i in range(n*n):
                    arr_1.append(float(i))

                for i in range(n*n):
                    arr_2.append(float(i))

                for i in range(n*n):
                    p_result_arr.append(0.0)
                    s_result_arr.append(0.0)

                if debug or (num_threads == 1 and not omp_threads_only):
                    s_start = time()
                    matrix_mult(arr_1, arr_2, s_result_arr, 0, n)
                    s_end = time()
                    serial_time = s_end - s_start

                if debug or num_threads > 1 or omp_threads_only:
                    p_start = time()
                    #omp parallel num_threads(num_threads)
                        chunk = int(n / num_threads)
                        id = omp_get_thread_num()
                        my_start = id * chunk
                        my_end = my_start + chunk if id < num_threads - 1 else n
                        matrix_mult(arr_1, arr_2, p_result_arr, my_start, my_end)

                    p_end = time()
                    parallel_time = p_end - p_start

                if debug:
                    if s_result_arr != p_result_arr:
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
                speedup_row.append(_runtime[0][x] / _runtime[i][x])
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
                efficiency_row.append(_runtime[0][x] / (_runtime[i][x] * val))
            writer.writerow(efficiency_row)
            efficiency_row = []