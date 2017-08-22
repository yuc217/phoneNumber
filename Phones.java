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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;


/**
 *
 * @author yuchencai
 */
public class Phones {
    
    // map holds code - counts
    Map<Integer, CountryCount> map; 
    PhoneNumberUtil phoneUtil;
    Map<Integer, String> codeMap;
    
    /**
     * constructor
     */
    Phones(){
        map = new HashMap<>();
        codeMap = new HashMap<>();
        phoneUtil = PhoneNumberUtil.getInstance();
    }
    
    /** 
     * read the second excel sheet 
     * @throws IOException 
     */
    public void loadFile(String path) throws IOException {
 
        //PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        String excelFilePath = path;
        FileInputStream inputStream = new FileInputStream(new File(excelFilePath));
         
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet firstSheet = workbook.getSheetAt(1); // carrier and phone
        Sheet secondSheet = workbook.getSheetAt(2); // country - code
        Iterator<Row> iterator = firstSheet.iterator();
        loadCountryCode(secondSheet);
        Row nextRow = iterator.next();
        double id;String carrier; int code=0;long phone=0;long parsed_number;
        // iterate through the sheet
        while (iterator.hasNext()) {
            nextRow = iterator.next();
            Iterator<Cell> cellIterator = nextRow.cellIterator();
            // id
            Cell cell = cellIterator.next();
            id = cell.getNumericCellValue();
            // carrier name
            cell = cellIterator.next();
            carrier = cell.getStringCellValue();
            // country code
            cell = cellIterator.next();
            code = (int) cell.getNumericCellValue();
            // phone number
            cell = cellIterator.next();
            phone = (long) cell.getNumericCellValue();
            // parse number as national number
            parsed_number = parseNumber(phone,String.valueOf(code).length());
            //System.out.println(parsed_number);
            PhoneNumber number = new PhoneNumber().setCountryCode(code).setNationalNumber(parsed_number);
            PhoneNumberType numberType = phoneUtil.getNumberType(number);
            // categorize by country
            categorizeNumber(numberType,code);          
        }       
  
        // close 
        workbook.close();
        inputStream.close();
        //System.out.println(count+"last: code:"+code+"number:"+phone);
        //printMap();
        //toSheet();
    }
    
    public void categorizeNumber(PhoneNumberType type, int code){       
        CountryCount obj;
        if(map.containsKey(code)){
            obj = map.get(code);
        } else {
            String name = codeMap.get(code);
            obj = new CountryCount(code,name);
        }
                //map.put(code, value);
                obj.addTotal();
                switch(type){
                    case MOBILE:
                    case FIXED_LINE_OR_MOBILE: obj.addMobile();break;
                    case FIXED_LINE: obj.addFix(); break;
                    case UNKNOWN: obj.addUnknown(); break;
                    default: obj.addOther();                
                }           
                // put back to map
                map.put(code, obj);   
    }
    
    
    public void printMap(){
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            System.out.println(pair.getKey() + " --- " + pair.getValue().toString());
            it.remove(); // avoids a ConcurrentModificationException
        }
    }
    
    public long parseNumber(long number, int n){
        String str = String.valueOf(number);
        return Long.parseLong(str.substring(n, str.length()));      
    }
    
    public void loadCountryCode(Sheet sheet){
        Iterator<Row> iterator = sheet.iterator();
        Row nextRow = iterator.next();
        String country; int code;
        while (iterator.hasNext()) {
            nextRow = iterator.next();            
            Iterator<Cell> cellIterator = nextRow.cellIterator();
            // country
            Cell cell = cellIterator.next();
            country = cell.getStringCellValue();
            //country code
            cell = cellIterator.next();
            code = (int) cell.getNumericCellValue();
            codeMap.put(code, country);
        }
            
    }
    
    public void toSheet(){
        XSSFWorkbook workbook = new XSSFWorkbook(); 
        XSSFSheet sheet = workbook.createSheet("typeRatioResult");
        //Set<String> keyset = data.keySet();
        Row row = sheet.createRow(0);
        Cell firstrow = row.createCell(0);
        firstrow.setCellValue("Country Code");
        firstrow = row.createCell(1);
        firstrow.setCellValue("Country Name");
        firstrow = row.createCell(2);
        firstrow.setCellValue("%Mobile");
        firstrow = row.createCell(3);
        firstrow.setCellValue("%Fixed Line");
        firstrow = row.createCell(4);
        firstrow.setCellValue("%Unknown");
               
        Iterator it = map.entrySet().iterator();
        int rownum = 1;
        while (it.hasNext()) {           
            row = sheet.createRow(rownum++);
           
            Map.Entry pair = (Map.Entry)it.next();
            //System.out.println(pair.getKey() + " --- " + pair.getValue().toString());
            Cell cell = row.createCell(0);
            cell.setCellValue((Integer)pair.getKey());
            cell = row.createCell(1);
            cell.setCellValue((String)((CountryCount)pair.getValue()).countryName);
            cell = row.createCell(2);
            cell.setCellValue((double)((CountryCount)pair.getValue()).mobileRatio());
            cell = row.createCell(3);
            cell.setCellValue((double)((CountryCount)pair.getValue()).fixRatio());
            cell = row.createCell(4);
            cell.setCellValue((double)((CountryCount)pair.getValue()).unknownRatio());
            it.remove(); // avoids a ConcurrentModificationException
        }
        
        try
        {
            //Write the workbook in file system
            FileOutputStream out = new FileOutputStream(new File("PhoneNumberResult.xlsx"));
            workbook.write(out);
            out.close();
            System.out.println("written successfully.");
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
  
    }
    
    
}
