#include <iostream>
#include <omp.h>
#include <stdlib.h>
#include <math.h>
using namespace std;

const int NUMBER_OF_RANDOMS = 10;
struct ThreadIndices {
	int startIndex, endIndex;
};
ThreadIndices partitionThreadIndices (int aStartIndex, int anEndIndex);


int slowMin (int anInt1, int anInt2) {
	cout << "Thread:" << omp_get_thread_num() << " calculating minimum of:" << anInt1 << " and " << anInt2 << endl;
	if (anInt1 > anInt2) {

		cout << "Thread:" << omp_get_thread_num() << " returning:" << anInt2  << endl;

		return anInt2;
	}
	cout << "Thread:" << omp_get_thread_num() << " returning:" << anInt1  << endl;
	return anInt1;
}

/*
 * Step 1
 * Run unmodified program (Eclipse Right Menu --> Run As --> Local C/C++ Application --> MyOpenMPTraining.exe
 * Number of threads: 1
 *
 * Step 2
 * Run unmodified program (Eclipse Right Menu --> Run As --> Local C/C++ Application --> MyOpenMPTraining.exe
 * Number of threads: 4
 *
 * You should see no difference in output, as we have not actually created multiple threads
 */
void computeAndPrintVariableNumberOfRandoms(int aNumberOfThreads) {
	/*
	 * Step 10.1.
	 * Uncomment the next declaration to so we can compute the minimum random
	 * number found by any thread. This must be a thread-global variable
	 * as it any thread could have the minimum random.
	 *
	 * Go to step 10.2
	 */
	int minRandomNumber = INT_MAX ;

	int aSeed, aRandomNumber;

/*
 * Step 8. Uncomment the parallel pragma below.
 * Comment back the parallel pragma  uncommented in Step 3.
 * (Eclipse: CTRL / works for commenting and uncommenting)
 *
 *
 * The private clause in the alternative parallel pragma below
 * tells OpenMP to automatically create a separate copy of the variables in parenthesed
 * (aRandomNumber and aSeed) for each thread that are not close to each other in
 * memory as array slots are.
 *
 * Comment back the critical pragma you uncommented in Step 7 as each
 * thread accesses a separate copy and thus does not need to coordinate its
 * actions with other threads.
 *
 * Rerun program (Eclipse: CTRL F11)
 * Number of threads: 4
 *
 * The output should show that each thread gets a separate seed and
 * prints a different random number.
 *
 * The next step shows a different way to solve this problem.
 *
 *
 */

//	#pragma omp parallel private (aRandomNumber, aSeed) num_threads(aNumberOfThreads)


/*
 * Step 3.
 * Uncomment the following parallel pragma (Eclipse: Select line and enter CTRL /)
 * The block after this pragma is now parallelized block.
 * Re run program (Eclipse: CTRL F11)
 * Number of threads: 1
 * A single random number is printed as before
 *
 * Step 4.
 * Rerun program (Eclipse: CTRL F11)
 * Number of threads: 4
 *
 * As many random number prints as number of threads, as each thread executes the
 * parallelized block - the block after the uncommented parallel pragma.
 * (a la multiple cooks executing the same recipe (compute random #) simultaneously
 *
 * But output is jumbled as all threads are printing at the same time
 * (a la multiple cooks announcing they are done at the same time)
 *
 *
 */
	#pragma omp parallel num_threads(aNumberOfThreads)
	{
	/*
	 * Step 9.1.
	 *
	 * Uncomment the declaration below to make aSeed and aRandomNumber
	 * not thread-global variables outside the parallelized block but
	 * thread-local variables inside the parallelized block.
	 * OpenMp makes all thread-local variables private, that is, creates different
	 * copies of the variables.
	 * We do not need a private clause to say they should be private.
	 *
	 * As we do not need the thread-global variables,
	 * you can comment out the declaration of these variables.
	 *
	 * We can Now go back to the original parallel pragma. So comment out the
	 * parallel paragma you uncommented in step 8 and uncomment the parallel
	 * pragma you commented in this step.
	 *
	 * Rerun program (Eclipse: CTRL F11)
	 * Number of threads: 4
	 *
	 * The output should show that each thread gets a separate seed and
	 * prints a different random number.
	 *
	 * This is an artifical problem in many ways.
	 *
	 * We need not have stored the random number (or seed) in a variable before using
	 * it, as shown below:
	 *
	 * cout << "Thread:" << omp_get_thread_num() << " Random:" << rand() << endl;
	 *
	 * The goal was to teach you about threads, critical regions, and private
	 * variables - hence the artificialty.
	 *
	 * The next step shows the use of these three concepts in a more realistic
	 * program where we do need a shared variable and critical region.
	 *
	 */

		int aSeed, aRandomNumber;

		/*
		 * Step 7.
		 * Uncomment the critical pragma below to ensure the statement block below
		 * is executed atomically, one at a time, so that between the set and use,
		 * No other thread can set the variable.
		 *
		 * Rerun program.
		 * Number of threads: 4
		 * Each thread sets its own seed.
		 * But some or all threads probably printed the same random number!
		 *
		 * This is because a single shared variable, aRandomNumber, holds the random
		 * number computed.
		 *
		 * Between the time a thread sets the variable and prints its value, some other thread
		 * can and probably does change the variable.
		 *
		 * We can compute and print the random number in a critical section.
		 *
		 * But now nothing is executed simultaneously in the program now. Why create
		 * multiple threads (employ multiple cooks),
		 * if they cannot perform any action simultaneously?
		 *
		 * The reason why we needed a critical section while accessing the seed
		 * number is that we used a single variable (a la note) for holding it.
		 *
		 * We also used a single variable for holding the random number computed,
		 * requiring us to use another critical region
		 *
		 * We can solve the problem by creating different variables for each thread.
		 * That is, we can create a shared array of variables, one element for each thread.
		 * int *aRandomNumbers = new int[aNumThreads];
		 *
 	 	 * Each thread then can then write in its slot in the array:
 	 	 * aRandomNumber[omp_get_thread_num()] = rand();
 	 	 *
		 * It can then print the value at that slot.
		 *  cout << "Thread:" << omp_get_thread_num() << " Random:" << aRandomNumberomp_get_thread_num() << endl;
		 *
		 * We can do so for both the random number and seed.
		 * This approach require us to do more work.
		 * Worse, it leads to a problem called false sharing (google for it),
		 * where different threads write to close by areas in memory,
		 * which leads to performance problems.
		 *
		 * The next step tells us one way we can solve this problem.
		 */
//		#pragma omp critical


		/*
		 * Step 6.
		 * Uncomment the block below (within curly braces). (Eclipse: Select block and enter CTRL /
		 * It starts each random number generator with a different seed - the thread number
		 * (A la different cooks being told to using a different variation of a dish)
		 * Rerun program.
		 * Number of threads: 4
		 *
		 * There are two kinds of outputs because of an extra output statement in this block.
		 * Each of the output statements is executed one at a time.
		 * But two different threads can execute different output statements at the same time.
		 * Each of the output statements is executed one at a time,
		 * but the two different output statements are printed simultaneously.
		 *
		 * (Each output statement corresponds to a different speaker, with its own queues.
		 * Two different threads can use different speakers simultaneously.)
		 *
		 * Also some or all threads use the same seed.
		 * This is because a single variable shared by all threads holds the seed.
		 * Each thread writes or sets the variable.
		 * But before it reads or uses the variable some other thread can change
		 * the variable.
		 * (a la before the recipe variation for a cook written on a shared note (piece of paper)
		 * is read by the cook, the recipe variation for another cook is written on
		 * the note.)
		 *
		 * We have already seen a way to solve the problem.
		 *
		 */
		{
			aSeed = omp_get_thread_num(); // set or write of seed to current thread number

			#pragma omp_critical
			cout << "Seed for thread:" << omp_get_thread_num() << " is "<< aSeed << endl ;

			srand(aSeed); // use or read of seed
		}


//		int aRandomNumber;

		aRandomNumber = rand();
		/*
		 * Step 5.
		 * Uncomment the line below.
		 * Rerun teh program (Eclipse: Select line and enter CTRL /)
		 * Number of threads: 4
		 * The statement block after the critical pragma, called a critical region,
		 * is executed "atomically," one at a time by the threads.
		 * (A la cooks having to queue up to use an intercom system,
		 * one cook using it at a time)
		 * The output is no longer jumbled.
		 *
		 * As before, the same random number is printed.
		 * This is because all threads use the same seed.
		 * (a la multiple cooks cooking the same variation of a dish.)
 	 	 *
		 */
		#pragma omp critical
		cout << "Thread:" << omp_get_thread_num() << " Random:" << aRandomNumber << endl;
		/*
		 * Step 11.
		 * Uncomment the pragma below to make the write to minRandomNumber atomic.
		 * Rerun the program with say 10 threads and validate that the correct
		 * minimum value is printed and slowMin is executed serially.
		 *
		 * The minumum value is printed by thread 0 - the original thread before - which
		 * is the only executable thread left after the parallelized block ends.
		 * When a block is parallelized, the specified number of threads become
		 * executable during the execution of the block. After the block ends,
		 * execution reverts to the original threads that were executable before
		 * the block was entered.
		 * (A la a group of cooks being hired during some stages of the cooking
		 * required for a party.)
		 *
		 * As mentioned earlier, this example was artificial in many ways so that
		 * the underlying concepts can be easily illustrated.
		 *
		 * A fundamental issue is that our parallelized versions of the program
		 * compute a different number of random numbers depending the number of threads created.
		 * (A a la, different numbers of dished being made depending on the
		 * number of available cooks.)
		 *
		 * This is why this function is called computeAndPrintVariableNumberOfRandoms.
		 *
		 * The number of threads should influence the time taken to compute some
		 * output by influencing how many activities can occur concurrently,
		 * but not the output.
		 *
		 * The next steps fixes this problem to illustrate how the task of printing a
		 * fixed number of random numbers (NUMBER_OF_RANDOMS) can be divided number of
		 * threads.
		 *
		 *
		 * Go back to main for the next step (12) by selecting main.cpp in the left Project
		 * Explorer window.
		 */
		#pragma omp critical

		/*
		 * step 10.2
		 * Uncomment the statement below to compute the new shared mimimum value
		 * Go to step 10.3

		 *
		 */
		minRandomNumber = slowMin (aRandomNumber, minRandomNumber);


	}
	/*
	 * step 10.3
	 * Uncommeng eeh statemenet below to print the  minimum random number.
	 * * Rereun the program with 4 threads.
	 * The traces should probably show the slowMin() is executed concurrently
	 * by different threads, that is, before a thread returns from the function
	 * another thread calls it. This means the two concurrent threads do not compare their
	 * values, and the shared variable may be set to either thread's random number,
	 * depending on which one writes last - not the smaller of the values.
	 * if you do not see this phenomenon, increase the number of threads, to for
	 * instance 10.
	 *
	 * Look at the code slowMain() in this file.
	 * (Eclipse: hover over the call to it below or click on the call and press F3)
	 *
	 * Unlike C's min, it prints output at entry and exit, which slows it down and thus
	 * increases the chance an execution of it by one thread
	 * can be interrupted by another thread's execution.
	 *
	 * We of course know the solution: any write to a thread-global variable should
	 * be in a critical section.
	 */
	cout << "Thread:" << omp_get_thread_num() << " prints minimum random:" << minRandomNumber << endl;

}

void computeAndPrintFixedNumberOfRandoms(int aNumThreads) {


	#pragma omp parallel num_threads(aNumThreads)
	{
		int aStartIndex = 0;
		int anEndIndex = NUMBER_OF_RANDOMS;

		srand(omp_get_thread_num());
		/*
		 * Step 13.
		 * The reason for a variable number of random numbers being output is that
		 * each thread executes the code for loop below in this parallelized block.
		 *
		 * We need to somehow divide the work among the threads.
		 *
		 * One way to do so is to divide the indices processed by the for loop below
		 * (O to NUMBER_OF_RANDOMS-1) among the available threads.
		 *
		 * The more the number of threads, the smaller the range processed by each
		 * thread.
		 *
		 * Uncomment the code block below and examine the code of partitionThreadIndices.
		 * (Eclipse: Hover on  or visit it (using F3))
		 *
		 * The function returns a partition of the index range passed to it that should
		 * be processed by the current thread, ensuring that the entire range is covered
		 * by the set of existing threads.
		 * As it returns two different values defining the partitioned range, the return
		 * type is a struct (declared at the top of the file) which is like a Java object
		 * with two instance variables.
		 *
		 * The uncommented code block reassigns the thread-local variables,
		 * aStartIndex and anEndIndex, to the values in the struct
		 * returned by partitionThreadIndices.
		 *
		 * rerun the code.
		 * The now contains a fixed number (NUMBER_OF_RANDOMS).
		 *
		 * It also shows the range of the indices of the original for loop
		 * (when aStartIndex and anEndIndex were not reassigned)
		 * assigned to each thread.
		 *
		 * The index range returned by partitionThreadIndices did not depend on
		 * what was done with the indices. Here the loop actually ignores the index
		 * and simply output a random. Another loop could use perform some computation
		 * based on the indices in the range, such as print the index, add it to some
		 * variable, or access an array.
		 *
		 * This means paritioning of an index range can be automated, which is what
		 * OpenMP does.
		 *
		 * Comment again the code block below and
		 *
		 * go to step 14.
		 */
//		{
//			ThreadIndices aThreadIndices = partitionThreadIndices (0, NUMBER_OF_RANDOMS);
//			aStartIndex = aThreadIndices.startIndex;
//			anEndIndex = aThreadIndices.endIndex;
//			#pragma omp critical
//			cout << "Thread:" << omp_get_thread_num()  << " allocated start index:" << aStartIndex << " and num end index: " << anEndIndex  << endl;
//		}

		/*
		 * Step 14.
		 *
		 * Uncomment the for pragma below.
		 * This tells OpenMP to assign to different threads different ranges of the indices
		 * of the range aStartIndex ... anEndIndex -1
		 * As these are  again the original values of these variables,
		 * the range is 0..NUMBER_OF_RANDOMS - 1
		 *
		 * Rerun the program.
		 * By replacing the code block above with the pragma below, we achieve our
		 * goal of dividing the work among multiple threads.
		 *
		 */

//		#pragma omp for

		for (int anIndex = aStartIndex; anIndex < anEndIndex; anIndex++) {
			#pragma omp critical
			cout << "Thread:" << omp_get_thread_num() << " index:" << anIndex << " random: " <<  rand() << endl;
		}

	}

}




