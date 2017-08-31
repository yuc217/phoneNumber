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
public class PhoneNumber {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        Phones phone = new Phones();
        try {
            //phone.test();
            //phone.loadFile("list.xlsx");                          
            // print ---- output to excel sheet
            //printSIMap();
            //toSheet();
            //carrierPercentSheet();
            //printCarrierMap();
            //System.out.println("null Count:" + carrierNullCount);
            //listCarriers();        
            //phone.loadResult();
            //phone.matchId();
 //           Map<Long, Integer> numbers = phone.loadTXT();
 //           phone.getPhoneType(numbers);
 //           phone.addCol();
            //phone.toSheet();
            //phone.printCarrierMap();
            //phone.carrierPercentSheet("carrier_percent_0829.xlsx");
            phone.test();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
