import java.util.*;

public class HashMap<K,V> {
    private List<Data<K,V>> datalist;

    public HashMap(){
        this.datalist=new ArrayList<Data<K,V>>();
    }

    public void addData(Data<K,V> x){ // this function also acts as a replace operator.
        //check if the same key exists before adding it.
        for(int i=0; i<datalist.size(); i++){
            Data temp=datalist.get(i);
            if(temp.key.equals(x.key)){
                //remove the existing Data
                datalist.remove(i);
                break;
            }
        }
        datalist.add(x);
    }

    public V getData(Data<K,V> x){
        for(int i=0; i<this.datalist.size(); i++){
            Data con = datalist.get(i);
            if (x.key.equals(con.key)) {
                return (V) con.value;
            }
        }
        return null;
    }


    public  static void main(String [] args){
        HashMap<Integer,String> hm = new HashMap<Integer, String>();
        Data<Integer,String> x = new Data<Integer, String>(1,"Panagia");
        Data<Integer,String> y = new Data<Integer, String>(2,"Xristos");
        Data<Integer,String> z = new Data<Integer, String>(3,"Agio Pneyma");
        hm.addData(x);
        hm.addData(y);
        hm.addData(z);

        System.out.println(hm.getData(x));
        x = new Data<Integer, String>(1, "OXI PANAGIA");
        hm.addData(x);
        System.out.println(hm.getData(x));
    }
}