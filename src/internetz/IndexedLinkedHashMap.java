package internetz;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class IndexedLinkedHashMap<K,V> extends LinkedHashMap{

	HashMap<Integer,Object> index;
	Integer curr = 0;

    @Override
    public Object put(Object key,Object value){
    	index.put(curr++, key);
        return super.put(key,value);
    }

    public Object getindexed(int i){
        return super.get(index.get(i));
    }

}