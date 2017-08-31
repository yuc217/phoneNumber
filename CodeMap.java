/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PhoneNumber;
import java.util.Map;
import java.util.HashMap;
/**
 *
 * @author yuchencai
 */
public class CodeMap {
    static Map<String, Integer> map = new HashMap<>();;
    
    public CodeMap(){
        
    }
    
    static Map<String,Integer> idCodeMapping(){
        map.put("AU",61);
        map.put("BD",880);
        map.put("BE",32);
        map.put("BR",55);
        map.put("CL",56);
        map.put("CO",57);
        map.put("CR",506);
        map.put("EG",20);
        map.put("FR",33);
        map.put("HK",852);
        map.put("IN",91);
        map.put("ID",62);
        map.put("MY",60);
        map.put("MX",52);
        map.put("NZ",64);
        map.put("PK",92);
        map.put("PE",51);
        map.put("PH",63);
        map.put("PL",48);
        map.put("RU",7);
        map.put("SG",65);
        map.put("ZA",27);
        map.put("LK",94);
        map.put("TH",66);
        map.put("AE",971);
        map.put("UA",380);
        map.put("GB",44);
        map.put("VN",84);
        return map;
    }
}
