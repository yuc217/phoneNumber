/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demo;
import com.google.i18n.phonenumbers.AsYouTypeFormatter;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberToCarrierMapper;
import com.google.i18n.phonenumbers.PhoneNumberUtil;

import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberType;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.google.i18n.phonenumbers.ShortNumberInfo;
//import com.google.i18n.phonenumbers.geocoding.PhoneNumberOfflineGeocoder;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
 
/**
 *
 * @author yuchencai
 */
public class Demo {
    
    /** 
     * read the second excel sheet 
     * @throws IOException 
     */
    public static void readFile() throws IOException {
        String excelFilePath = "list.xlsx";
        FileInputStream inputStream = new FileInputStream(new File(excelFilePath));
         
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet firstSheet = workbook.getSheetAt(1);
        Iterator<Row> iterator = firstSheet.iterator();
        
        int count =0;
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
            // code
            cell = cellIterator.next();
            double code = cell.getNumericCellValue();
            // phone number
            cell = cellIterator.next();
            double phone = cell.getNumericCellValue();
                            
            System.out.println("carrier:"+carrier+",phone number:"+phone);
            
            //count++;
            //if(count>=10) break;
        }
         
        workbook.close();
        inputStream.close();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try{
            readFile();
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    
}
