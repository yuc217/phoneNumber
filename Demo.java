/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demo;
 
/**
 *
 * @author yuchencai
 */
public class Demo {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        Phones phone = new Phones();
        try{
            phone.loadFile("list.xlsx");
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    
}
