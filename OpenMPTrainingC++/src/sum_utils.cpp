/*
 * The purpose of this exercise is to teach you four different ways of using
 * OpenMP to parallelize a sequential C++ program for summing the
 * first N natural numbers.
 *
 * The sequential solution is in the file:
 *
 * sum_sequential.cpp.
 *
 * The solutions you will create will in the files with the term parallel:
 *
 * sum_parallel_for_local_var.cpp
 * sum_parallel_for_reduction.cpp
 * sum_parallel.cpp
 * sum_sequential.cpp
 *
 * Each of these files is a copy of the sequential solution.
 * Your task is to modify them to create the four OpenMP solutions.
 *
 * Each file defines a procedure or method that can be called from a main
 * program. This file provides the main function, which calls the sequential
 * solution and all four parallel solutions.
 *
 * The unmodified parallel solutions will have the same output as the sequential
 * solution. The modified solution will compute the same result but print different
 * output. This is because the sequential and parallel solutions print not only
 *
 */

#include <iostream> // for C++ I/O
#include <omp.h> // for open mp calls

using namespace std;// for C++ IO
//need to declare headers of external functions called
void sum_sequential(double* aNumberArray, int aSize);
void sum_parallel_for_reduction(double* aNumberArray, int aSize);
void sum_parallel(double* aNumberArray, int aSize);
void sum_parallel_for_local_var(double* aNumberArray, int aSize);

const int SIZE = 1000;

/**
 * Called by each of the summing alternatives
 */
void printSumWithTimes (int aSum, int aStartTime, int anEndTime) {
	cout << "Sum: " << aSum << endl;
	cout << "Took: " << anEndTime - aStartTime << endl;
}
void printNumThreads() {
	cout << "threads:" << omp_get_num_threads() << endl;
}
/**
 * an array to learn how much concurrency occurred
 */
int MAX_THREADS = 100;
double* threadsLastIndex = new double[MAX_THREADS];
int numThreads;

//void printIntArray (int* anArray, int aSize) {
//	cout << "Int Array:" << endl;
//	for (int i = 0; i < aSize; i++) {
//		cout << " Index:" << i << " Value: " << anArray[i] << endl;
//	}
//}

struct ThreadIndices {
	int startIndex, endIndex;
};

ThreadIndices partitionThreadIndices (int aStartIndex, int anEndIndex) {
	int aNumberOfIterations = anEndIndex - aStartIndex;
	int aThreadNumIterations = aNumberOfIterations/omp_get_num_threads();
	int aThreadStartIndex = aThreadNumIterations*omp_get_thread_num();
	int aRemainingIterations = aNumberOfIterations - (aThreadNumIterations*omp_get_num_threads());
	if (omp_get_thread_num() == omp_get_num_threads() -1 ) {
			aThreadNumIterations += aRemainingIterations;
	}
	ThreadIndices retVal;
	retVal.startIndex = aThreadStartIndex;
	retVal.endIndex = aThreadStartIndex + aThreadNumIterations;
	return retVal;

}

int readNumThreads() {
	cout << "Number of threads?";
	int retVal;
	cin >> retVal;
	return retVal;
}

void recordThreadIndex(int anIndex) {
	int thread =  omp_get_thread_num();
	if (thread >= MAX_THREADS) {
		return;
	}
	threadsLastIndex[thread] = anIndex;
	numThreads = omp_get_num_threads();
}
void initThreadRecording() {
	numThreads = 0;
	for (int i = 0; i < MAX_THREADS; i++) {
		threadsLastIndex[i] = -1;
	}
}
void initArray (double* aNumberArray, int aSize ) {
	for(int i = 0; i < aSize; i++) {
			aNumberArray[i]=i;
	}
}
void initArrayAndTracing (double* aNumberArray, int aSize ) {
	initArray(aNumberArray, aSize);
	initThreadRecording();
}

void printThreadLastIndices() {
	for (int i = 0; i < numThreads; i++) {
		cout << "thread:" << i << " lastIndex:" << threadsLastIndex[i] << endl;
	}
	cout << "_____________________________" << endl;
}



void sum_driver()
{
	double* aNumberArray = new double[SIZE];
	initArray(aNumberArray, SIZE);
	initThreadRecording();
	sum_sequential(aNumberArray, SIZE);
	printThreadLastIndices();
	initThreadRecording();
	sum_parallel_for_reduction(aNumberArray, SIZE);
	printThreadLastIndices();
	initThreadRecording();
	sum_parallel_for_local_var(aNumberArray, SIZE);
	printThreadLastIndices();
	initThreadRecording();
	sum_parallel(aNumberArray, SIZE);
	printThreadLastIndices();
	initThreadRecording();
}
