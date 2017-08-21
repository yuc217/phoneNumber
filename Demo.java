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
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber.CountryCodeSource;
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
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        /**
        long ps = 32473683036L;
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        //PhoneNumber num = phoneUtil.format(ps, PhoneNumberFormat.NATIONAL);
        PhoneNumber numberA = new PhoneNumber();
        numberA.setRawInput("32 473 683 036").setCountryCodeSource(CountryCodeSource.FROM_NUMBER_WITHOUT_PLUS_SIGN);
        //PhoneNumber number = new PhoneNumber(32).setNationalNumber();
        PhoneNumberType numberType = phoneUtil.getNumberType(numberA);
        System.out.println("type:"+numberType);
       */
        Phones phone = new Phones();
        try{
            phone.loadFile("list.xlsx");
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    
}
