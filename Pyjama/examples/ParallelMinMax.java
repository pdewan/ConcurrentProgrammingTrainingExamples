import java.util.concurrent.ThreadLocalRandom;

public class ParallelMinMax {

    public static void main(String[] args) {

        int numThreads = Integer.parseInt(args[0]);
        int n = Integer.parseInt(args[1]);
        int max_val = 0;
        int [] arr = new int[n];
        for (int i = 0; i < n; i++) 
            arr[i] = ThreadLocalRandom.current().nextInt(0, 101);
        
        //#omp parallel num_threads(numThreads) shared(n, arr) reduction(max:max_val)
        {
            //#omp for
            for (int i = 0; i < n; i++)
            {
                if (arr[i] > max_val)
                    max_val = arr[i];
            }
        }

        for (int i = 0; i < n; i++) System.out.print(arr[i] + " ");
        System.out.println("\nmax value = " + max_val);
    }
}
