#include <iostream>
#include <omp.h>
using namespace std;// for C++ IO

void computeAndPrintVariableNumberOfRandoms  (int aNumberOfThreads);
void computeAndPrintFixedNumberOfRandoms (int aNumberOfThreads);
int readNumThreads();

//
//int readNumThreads() {
//	cout << "Number of threads?";
//	int retVal;
//	cin >> retVal;
//	return retVal;
//}

int main()
{
	double aStartTime = omp_get_wtime();

//	sum_driver();

	int aNumberOfThreads = readNumThreads();
	cout << "Number of threads:" << aNumberOfThreads << endl;
	/*
	 * Step 12
	 * Comment out the call to computeAndPrintVariableNumberOfRandoms.
	 * Uncomment the call to computeAndPrintFixedNumberOfRandoms.
	 *
	 * Our goal is to make the program print NUMBER_OF_RANDOMS random
	 * numbers, but the program prints aNumberOfThreads*NUMBER_OF_RANDOMS!
	 *
	 * To understand the output and learn how to fix it,
	 * go to Step 13 in the function computeAndPrintFixedNumberOfRandoms.
	 * (Eclipse: Put the cursor on the call to this function below and click F3.
	 * Or Ctrl left arrow should also work as you just came here from it.)
	 * You can also select it from the explorer window on the left, and click on
	 * random.cpp to open the file and search for the function in it.
	 *
	 */
//	computeAndPrintVariableNumberOfRandoms(aNumberOfThreads);
	computeAndPrintFixedNumberOfRandoms(aNumberOfThreads);


//	computeRandomsInParallelAndPrintRandoms(aNumberOfThreads);

	double anEndTime = omp_get_wtime();
	cout << "Time Taken:" << anEndTime - aStartTime << endl;


}
