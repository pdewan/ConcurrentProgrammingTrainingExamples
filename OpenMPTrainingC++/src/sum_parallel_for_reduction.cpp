#include <iostream>
#include <omp.h>
using namespace std;

void initArrayAndTracing (double* aNumberArray, int aSize);
void printThreadLastIndices();
void printSumWithTimes(int sum, int start, int end);
void printNumThreads();
void recordThreadIndex(int index);

const int SIZE = 1000;

void sum_parallel_for_reduction(double* array, int size) {
	cout << "sum_parallel_for_reduction\n";

	double start = omp_get_wtime();

	double sum = 0;

	for (int i = 0; i < size; i++) {
		sum += array[i];
		recordThreadIndex(i);
	}

	double end = omp_get_wtime();

	printSumWithTimes(sum, start, end);

}

void sum_parallel_for_reduction_main() {
	double* aNumberArray = new double[SIZE];
	initArrayAndTracing(aNumberArray, SIZE);
	sum_parallel_for_reduction(aNumberArray, SIZE);
	printThreadLastIndices();
}

//int main() {
//	sum_parallel_for_reduction_driver();
//}
