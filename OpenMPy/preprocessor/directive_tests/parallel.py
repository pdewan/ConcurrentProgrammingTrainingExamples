#omp parallel num_threads(5)
    #omp critical
        print('thread ', omp_get_thread_num(), ' of ', omp_get_num_threads())