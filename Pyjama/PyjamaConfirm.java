public class PyjamaConfirm
{
	
	public static void main(String[] args) 
	{
		//#omp parallel num_threads(2)
		{
			int a = Pyjama.omp_get_thread_num();
			//#omp critical
			{
				System.out.printf("hi from thread %d\n", a);
			}
		}
	}
}