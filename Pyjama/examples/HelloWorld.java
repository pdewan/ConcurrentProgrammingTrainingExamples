public class HelloWorld {
    
    public static void main(String[] args) {
        
        int threadCount = Integer.parseInt(args[0]);

        //#omp parallel num_threads(threadCount)
        {
            int myID = Pyjama.omp_get_thread_num();
            int tCount = Pyjama.omp_get_num_threads();
            System.out.println("Hello from "+myID +" of "+tCount);
        }
    }
}
