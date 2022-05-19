num = 0
#omp parallel num_threads(10) shared(num)
    #omp master
        num += 1
print('num == 1: ', num == 1)
