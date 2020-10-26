import java.util.*;

// << +1 || >> -1

public class ExtHash<K,V> {
    int noofbuckets;
    List<Page<K,V>> buckets;
    int size; //size of array list
    int global_depth = 0;

    public ExtHash(int noofbuckets){
        this.noofbuckets = noofbuckets;
        buckets = new ArrayList<Page<K,V>>();
        }

    public static String hashCode(Data x) {
        //binary hashcode
        String temp = Integer.toBinaryString((Integer) x.key);
        String hashcode = String.format("%32s", temp).replaceAll(" ", "0");  // 32-bit Integer
        return hashcode;
    }

    public void addData(Data<K,V> x){}

    public void addPage(){
        buckets.add(new Page<K,V>());
    }

    public Page<K,V> getPage(Data x){
        String hashcode = hashCode(x);
        //Page<K,V> p = buckets.get()
        return null;
    }

    public void removeData(Data<K,V> x){ }

    public Page<K,V> get(Data<K,V> x){
        int h = x.key.hashCode();
        //Page<K,V> temp =
        return null;
    }

    @Override
    public String toString() {
        return "ExtHash{" +
                "noofbuckets=" + noofbuckets +
                ", buckets=" + buckets +
                '}';
    }
    /*
    public V get(K key)
    {
        //Find head of bucket
        int bucketIndex = hashFunction(key);
        Data<K, V> head = buckets.get(bucketIndex);

        while (head != null)
        {
            if (head.key.equals(key))
                return head.value;
            head = head.next;
        }

        // If key not found
        return null;
    }
    */

    /*
    public void show() {
        for(int i=0; i<noofbuckets; i++){
            System.out.print(i);
            for(j : buckets.get(i)){
                Data<K,V> o = buckets.get(i);
                System.out.print("-->" + o);
            }
        }
    }
    */

    public static void main(String [] args){
        //System.out.println(x);
        Data<Integer,Integer> z = new Data<>(4,10);
        Data<Integer,Integer> y = new Data<>(5,15);
        System.out.println(hashCode(z));

    }
}
