package github;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public abstract class TaskSkillFrequency {

    public static final Map<String, Double> frequency;
    static
    {
    	frequency = new HashMap<String, Double>();
    	frequency.put("SMALL", 40d);
    	frequency.put("MEDIUM", 250d);
    	frequency.put("SMALL", 1000d);
    	frequency.put("MEDIUM", 8500d);
    }
    
    public static BigInteger tasksCheckSum;
    
    public static void clear(){
    	tasksCheckSum = BigInteger.ZERO;
    }

}
