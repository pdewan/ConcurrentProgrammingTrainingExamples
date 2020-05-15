#include <iostream>
#include <omp.h>
using namespace std;

void initArrayAndTracing (double* aNumberArray, int aSize);
void printThreadLastIndices();
void printSumWithTimes(int sum, int start, int end);
void printNumThreads();
void recordThreadIndex(int index);

const int SIZE = 1000;

void sum_parallel(double* aNumberArray, int aSize) {
	cout << "sum_parallel\n";

	double start = omp_get_wtime();

	double sum = 0;

	for (int i = 0; i < aSize; i++) {
		sum += aNumberArray[i];
		recordThreadIndex(i);
	}

	double end = omp_get_wtime();

	printSumWithTimes(sum, start, end);

}

void sum_parallel_main() {
	double* aNumberArray = new double[SIZE];
	initArrayAndTracing(aNumberArray, SIZE);
	sum_parallel(aNumberArray, SIZE);
	printThreadLastIndices();
}

//int main() {
//	sum_parallel_driver();
//}

