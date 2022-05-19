sum = 0
#omp parallel num_threads(2) shared(sum)
    for x in range(100):
        sum += 1
print('sum != 200: ', sum != 200)


sum = 0
#omp parallel num_threads(2) shared(sum)
    for x in range(100):
        #omp atomic
        sum += 1
print('sum == 200: ', sum == 200)


