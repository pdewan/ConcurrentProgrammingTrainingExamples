# + reduction
var = 0
#omp parallel num_threads(10) reduction(+:var)
    var = 1
print('var == 10: ', var == 10)


# - reduction
var = 0
#omp parallel num_threads(10) reduction(-:var)
    var = 1
print('var == 10: ', var == 10)


# * reduction
var = 0
#omp parallel num_threads(10) reduction(*:var)
    var = 1
print('var == 1: ', var == 1)


# logical AND reduction
var = 0
#omp parallel num_threads(10) reduction(&&:var)
    var = 1
print('var == True: ', var == True)


# logical OR reduction
var = 0
#omp parallel num_threads(10) reduction(||:var)
    var = 0
print('var == False: ', var == False)


# bit-level AND reduction
var = 0
#omp parallel num_threads(10) reduction(&:var)
    var = 1
print('var == 1: ', var == 1)


# bit-level OR reduction
var = 0
#omp parallel num_threads(10) reduction(|:var)
    var = 0
print('var == 0: ', var == 0)


# max reduction
var = 0
#omp parallel num_threads(10) reduction(max:var)
    var = omp_get_thread_num()
print('var == 9: ', var == 9)


# min reduction
var = 0
#omp parallel num_threads(10) reduction(min:var)
    var = omp_get_thread_num()
print('var == 0: ', var == 0)


# bit-level XOR reduction
var = 0
#omp parallel num_threads(10) reduction(^:var)
    var = 1
print('var == False: ', var == False)



