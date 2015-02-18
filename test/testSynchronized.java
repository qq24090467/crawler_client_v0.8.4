public class testSynchronized implements Runnable {

	@Override
	public void run() {
//		synchronized (this) 
		{
			for (int i = 0; i < 5; i++) {
				System.out.println(Thread.currentThread().getName() + " syn loop" + i);
			}

		}
	}

	public static void main(String[] args) {
		testSynchronized t1 = new testSynchronized();
		
		Thread a = new Thread(t1, "a");
		Thread b = new Thread(t1, "b");
		a.start();
		b.start();
	}

}
