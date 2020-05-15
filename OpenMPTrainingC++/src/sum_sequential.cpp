#include <iostream>
#include <omp.h>
using namespace std;

void initArrayAndTracing (double* aNumbers, int aSize);
void printThreadLastIndices();
void printSumWithTimes(int sum, int start, int end);
void printNumThreads();
void recordThreadIndex(int index);

const int SIZE = 1000;

void sum_sequential(double* aNumbers, int aSize) {

	cout << "sum_sequential\n";

	double aStartTime = omp_get_wtime();

	double aSum = 0;

	for (int i = 0; i < aSize; i++) {
		aSum += aNumbers[i];
		recordThreadIndex(i);
	}

	double anEndTime = omp_get_wtime();

	printSumWithTimes(aSum, aStartTime, anEndTime);
}

void sum_sequential_main() {
	double* aNumbers = new double[SIZE];
	initArrayAndTracing(aNumbers, SIZE);
	sum_sequential(aNumbers, SIZE);
	printThreadLastIndices();
}

