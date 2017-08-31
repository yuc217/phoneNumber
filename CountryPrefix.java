/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PhoneNumber;
import java.io.File;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;
import java.io.FileNotFoundException;

/**
 *
 * @author yuchencai
 */
public class CountryPrefix {
    
    static Map<Integer, String> map = new HashMap<>();;
    
    public CountryPrefix(){
        
    }

        
    static public Map<Integer, String> nationalcode() {
        File sourceFile = new File("nationalNumber.txt");
        // Scanner class check if it is valid file
        Scanner input = null;
        try {
            input = new Scanner(sourceFile);
        } catch (FileNotFoundException ex) {
            System.out.println("Error: Couldn't open file");
            System.exit(1);
        }
        String line;
        while (input.hasNextLine()) {
            line = input.nextLine();
            String[] tokens = line.split("-");
            
            map.put(Integer.parseInt(tokens[1]),tokens[0]);
        }
        
        
        return map;
    }
}
