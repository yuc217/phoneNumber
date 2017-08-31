/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PhoneNumber;
import java.util.Map;
/**
 *
 * @author yuchencai
 */
public class Carrier {
    
    //int id;
    String name;
    String country;
    
    public Carrier(String name, String country){
        this.name = name;
        this.country = country;
    }
    
    public boolean compare(Map.Entry pair){
        //System.out.println(pair.getKey()+"-"+pair.getValue()+":"+name+"-"+country);
        // str: shorter
        String str = (String)pair.getKey();
        str = str.toLowerCase();
        int code = (int)pair.getValue();
        String full = name.toLowerCase();
        if(full.contains(str)||str.contains(full)){
            //System.out.println(pair.getKey()+"-"+pair.getValue()+":"+name+"-"+country);
            Map<String,Integer> map = CodeMap.idCodeMapping();
            
            if(map.get(this.country)!=null) return code==map.get(this.country);
        }
        return false;
    }
}
