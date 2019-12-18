#include <iostream>
#include <omp.h>

using namespace std;
void sum_sequential(double* array, int size);
void sum_parallel_for_reduction(double* array, int size);
void sum_parallel(double* array, int size);
void sum_parallel_for_local_var(double* array, int size);

const int SIZE = 1000;
void initArray (double* array, int size ) {
	for(int i = 0; i < size; i++)
			array[i]=i;
}
void printSumWithTimes (int sum, int start, int end) {
	cout << "Sum: " << sum << endl;
	cout << "Took: " << end - start << endl;
}
void printNumThreads() {
	cout << "threads:" << omp_get_num_threads() << endl;
}
int main()
{
	double* array = new double[SIZE];
	initArray(array, SIZE);
	sum_sequential(array, SIZE);
	sum_parallel_for_reduction(array, SIZE);
	sum_parallel_for_local_var(array, SIZE);
	sum_parallel(array, SIZE);

}
