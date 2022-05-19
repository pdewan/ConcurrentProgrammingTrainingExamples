sum = 0
#omp parallel num_threads(10) shared(sum)
    #omp for schedule(static, 5)
        for i in range(100):
           #omp critical
                sum += 1
print('sum == 100: ', sum == 100)
