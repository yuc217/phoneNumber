/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demo;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberType;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author yuchencai
 */
public class Phones {
    
    // map holds phone numbers - carrier
    Map<Integer, CountryCount> map; 
    PhoneNumberUtil phoneUtil;
    
    /**
     * constructor
     */
    Phones(){
        map = new HashMap<>();
        phoneUtil = PhoneNumberUtil.getInstance();
    }
    
    /** 
     * read the second excel sheet 
     * @throws IOException 
     */
    public void loadFile(String path) throws IOException {
        
        int totalCount = 0;
        int mobileCount = 0;
       
        int fixCount = 0;
        int unknownCount = 0;
        int other = 0;
        
        //PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        String excelFilePath = path;
        FileInputStream inputStream = new FileInputStream(new File(excelFilePath));
         
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet firstSheet = workbook.getSheetAt(1);
        Iterator<Row> iterator = firstSheet.iterator();
        
        int count =0;
        int countryCode = 0;
        Row nextRow = iterator.next();
        while (iterator.hasNext()) {
            
            nextRow = iterator.next();
            
            Iterator<Cell> cellIterator = nextRow.cellIterator();
            // id
            Cell cell = cellIterator.next();
            double id = cell.getNumericCellValue();
            // carrier name
            cell = cellIterator.next();
            String carrier = cell.getStringCellValue();
            // country code
            cell = cellIterator.next();
            int code = (int) cell.getNumericCellValue();
            // phone number
            cell = cellIterator.next();
            long phone = (long) cell.getNumericCellValue();
            // parse number as national number
            long parsed_number = parseNumber(phone,String.valueOf(code).length());
            //System.out.println(parsed_number);
            /**
            if(countryCode ==0) countryCode = code;
            else if(countryCode != code){
                System.out.println("in "+countryCode+", total:"+totalCount+","
                        + "Mobile:"+mobileCount+",fixed:"+fixCount+",unknown:"+unknownCount
                        + ", other:"+other);
                countryCode = code;
                totalCount = 0; mobileCount = 0; fixCount = 0; unknownCount = 0;
                other = 0;
            }else{*/
                // get number type 
                PhoneNumber number = new PhoneNumber().setCountryCode(code).setNationalNumber(parsed_number);
                PhoneNumberType numberType = phoneUtil.getNumberType(number);
                totalCount++;
                
                //map.put(code, value)
                switch(numberType){
                    case MOBILE:
                    case FIXED_LINE_OR_MOBILE: mobileCount++;break;
                    case FIXED_LINE: fixCount++; break;
                    case UNKNOWN: unknownCount++; break;
                    default: other++;                
                }
            
            //}              
        }
        // close 
        System.out.println("end");
        workbook.close();
        inputStream.close();
      
        //printMap();
    }
    
    
    public void printMap(){
        Iterator it = phone_carrier_map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            System.out.println(pair.getKey() + " = " + pair.getValue());
            it.remove(); // avoids a ConcurrentModificationException
        }
    }
    
    public long parseNumber(long number, int n){
        String str = String.valueOf(number);
        return Long.parseLong(str.substring(n, str.length()));      
    }
}
