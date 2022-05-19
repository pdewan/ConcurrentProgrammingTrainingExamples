import jarray
from time import time
import csv
from datetime import datetime
import os


if __name__ == '__main__':
    debug = False               # if set to true, serial and omp will both be run and results are compared at the end
    omp_threads_only = True
    platform = 'windows'
    benchmark = 'sum'
    num_runs = 20               # number of runs for each thread/n combination. average across runs is stored at the end
    n_range = range(500000, 105000000, 5000000)
    thread_list = [1, 2, 4, 8, 12]

    j_home = os.getenv('JYTHON_HOME') if os.getenv('JYTHON_HOME').endswith('/') else os.getenv('JYTHON_HOME') + '/'
    result_dir = j_home + 'preprocessor/benchmark_results/' + platform + ('/omp_threads_only/' if omp_threads_only else '/standard/') + benchmark + '/'

    # create result directory if needed
    if not os.path.exists(result_dir):
        try:
            os.makedirs(os.path.dirname(result_dir + '.keep'))
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

                arr = jarray.array([], int)

                s_sum = 0
                p_sum = 0
                serial_time = 9999999999999
                parallel_time = 9999999999999

                for i in range(n):
                    arr.append(1)

                if debug or (num_threads == 1 and not omp_threads_only):
                    s_start = time()
                    for i in range(n):
                        s_sum += arr[i]
                    s_end = time()
                    serial_time = s_end - s_start

                if debug or num_threads > 1 or omp_threads_only:
                    p_start = time()
                    #omp parallel num_threads(num_threads) reduction(+:p_sum)
                        #omp for schedule(static)
                            for x in range(len(arr)):
                                p_sum += arr[x]
                    p_end = time()
                    parallel_time = p_end - p_start

                if debug:
                    if s_sum != p_sum:
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