#include <iostream>
#include <omp.h>

using namespace std;
//need to declare headers of external functions called
void sum_sequential(double* array, int size);
void sum_parallel_for_reduction(double* array, int size);
void sum_parallel(double* array, int size);
void sum_parallel_for_local_var(double* array, int size);

// if this main is used to call functions to do other computations
// then those headers should also be declared here

const int SIZE = 1000;
void initArray (double* array, int size ) {
	for(int i = 0; i < size; i++)
			array[i]=i;
}
/**
 * Called by each of the summing alternatives
 */
void printSumWithTimes (int sum, int start, int end) {
	cout << "Sum: " << sum << endl;
	cout << "Took: " << end - start << endl;
}
void printNumThreads() {
	cout << "threads:" << omp_get_num_threads() << endl;
}
/**
 * an array to learn how much concurrency occcurred
 */
int MAX_THREADS = 100;
double* threadsLastIndex = new double[MAX_THREADS];
int numThreads;


void recordThreadIndex(int index) {
	int thread =  omp_get_thread_num();
	if (thread >= MAX_THREADS) {
		return;
	}
	threadsLastIndex[thread] = index;
	numThreads = omp_get_num_threads();
}
void initThreadRecording() {
	numThreads = 0;
	for (int i = 0; i < MAX_THREADS; i++) {
		threadsLastIndex[i] = -1;
	}
}

void printThreadLastIndex() {
	for (int i = 0; i < numThreads; i++) {
		cout << "thread:" << i << " lastIndex:" << threadsLastIndex[i] << endl;
	}
	cout << "_____________________________" << endl;
}


int main()
{
	double* array = new double[SIZE];
	initArray(array, SIZE);
	initThreadRecording();
	sum_sequential(array, SIZE);
	printThreadLastIndex();
	initThreadRecording();
	sum_parallel_for_reduction(array, SIZE);
	printThreadLastIndex();
	initThreadRecording();
	sum_parallel_for_local_var(array, SIZE);
	printThreadLastIndex();
	initThreadRecording();
	sum_parallel(array, SIZE);
	printThreadLastIndex();
	initThreadRecording();
}
