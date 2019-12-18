#include <iostream>
#include <omp.h>

using namespace std;
void printSumWithTimes(int sum, int start, int end);
void printNumThreads();
void recordThreadIndex(int index);
void sum_sequential(double* array, int size) {

	cout << "sum_sequential\n";

	double start = omp_get_wtime();

	double sum = 0;

	for (int i = 0; i < size; i++) {
		sum += array[i];
		recordThreadIndex(i);
	}

	double end = omp_get_wtime();

	printSumWithTimes(sum, start, end);
}
