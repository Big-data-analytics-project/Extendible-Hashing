import java.util.*;

public class MyThread<K,V> implements Runnable {
    String name;
    Thread t;
    ExtHash eh;
    List<String> keys;
    List<String> values;
    String operation;
    List<Long> times = new ArrayList<>();

    public MyThread(){};

    public MyThread(String name, ExtHash eh,  List<String> keys, List<String> values, String operation) {
        this.name = name;
        this.eh = eh;
        this.keys = keys;
        this.values = values;
        this.operation = operation;
    }

    public void run() {
        if (operation == "Insert") {
            for (int i = 0; i < keys.size(); i++)
                eh.put(keys.get(i), values.get(i));
        } else if (operation == "Search") {
        	for(int i=0; i<keys.size(); i++)
        		eh.getValue(keys.get(i));
        }
    }

    public List<Long> getList(){
        return times;
    }

}
