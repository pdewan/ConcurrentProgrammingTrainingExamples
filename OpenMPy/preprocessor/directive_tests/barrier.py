from time import sleep

#omp parallel num_threads(2)
    if omp_get_thread_num() == 0:
        sleep(3)
    else:
        print('thread: ', omp_get_thread_num(), ' waiting at barrier...')
    #omp barrier
    #omp critical
        print('thread: ', omp_get_thread_num(), ' made it through barrier')

