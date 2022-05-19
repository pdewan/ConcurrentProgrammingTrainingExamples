sum = 0
#omp parallel for num_threads(10) schedule(static, 5) shared(sum)
    for i in range(100):
       #omp critical
            sum += 1
print('sum == 100: ', sum == 100)
