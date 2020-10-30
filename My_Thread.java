
public class My_Thread<K,V> extends Thread{
	ExtHash<K,V> d;
	int K_;
	int V_;
	
	My_Thread(ExtHash d){
		this.d = d;
		this.K_ = K_;
		this.V_ = V_;
	}
	
	
	public void run() {
		// TODO Auto-generated method stub
		System.out.println(d);
	}
	
	
}
