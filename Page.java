import java.util.*;

public class Page<K,V> {
    final int max_size = 20;
    int localdepth = 0;
    HashMap<K,V> temp;
    List t = new ArrayList<Data>();

    void addData(Data x){
        t.add(x);
    }
    boolean isFull(){
        return t.size() > max_size;
    }
}
