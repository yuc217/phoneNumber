/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PhoneNumber;

import com.google.i18n.phonenumbers.PhoneNumberToCarrierMapper;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberType;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
//import java.util.ArrayList;

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
    PhoneNumberUtil phoneUtil;
    PhoneNumberToCarrierMapper carrierMapper;
    int carrierNullCount = 0;
    
    Map<Integer, CountryCount> map;// map holds code - counts
    Map<Integer, String> codeMap;
    Map<Integer, Map<String, Integer>> carrierListMap;  // code -> map | carrierName -> count
    Map<Integer, Carrier> idMap; // network ID - carrier name + country
    Map<String, Integer> carriers;
    Map<Integer, String> prefix = CountryPrefix.nationalcode();

    /**
     * constructor
     */
    Phones() {
        phoneUtil = PhoneNumberUtil.getInstance();
        carrierMapper = carrierMapper = PhoneNumberToCarrierMapper.getInstance();
        
        map = new HashMap<>();
        codeMap = new HashMap<>();
        carrierListMap = new HashMap<>();
        carriers = new HashMap<>();
        idMap = new HashMap<>();
    }

    /**
     * reading .txt file - list of numbers
     * @return map <phone number, country code>
     * @throws IOException 
     */
    public Map<Long, Integer> loadTXT() throws IOException {
        Map<Long, Integer> number = new HashMap<>();
        File sourceFile = new File("Kirk_cc_phone_list_no_US_170825.txt");
        Scanner input = null;
        try {
            input = new Scanner(sourceFile);
        } catch (FileNotFoundException ex) {
            System.out.println("Error: Couldn't open file");
            System.exit(1);
        }
        input.nextLine();
        while (input.hasNextLine()) {
            String line = input.nextLine().substring(1);
            //boolean flag = true;  // checking if there is number not getting mapped
            for (int code : prefix.keySet()) {
                if (line.startsWith(code + "")) {
                    //flag = false;
                    try {
                        number.put(parseNumber(Long.parseLong(line), (code + "").length()), code);
                    } catch (NumberFormatException e) {
                        System.out.println("exception on number:" + line);
                    }
                }
            }
            //if(flag) System.out.println(line);
        }
        return number;
    }

    /**
     * categorize numbers into mobile, fixed line, unknown
     * @param nationalphones 
     */
    public void getPhoneType(Map<Long, Integer> nationalphones) {
        for (Map.Entry pair : nationalphones.entrySet()) {
            int code = (int) pair.getValue();
            Long num = (Long) pair.getKey();
            PhoneNumber number = new PhoneNumber().setCountryCode(code).setNationalNumber(num);
            PhoneNumberType numberType = phoneUtil.getNumberType(number);
            // categorize by country
            categorizeNumber(numberType, code, num);
        }
        //for(Map.Entry entry : map.entrySet()){
        //    System.out.println(entry.getKey()+((CountryCount)entry.getValue()).toString());
        //}
    }

    /**
     * read the excel sheets
     * phase 1
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
        double id;
        String carrier;
        int code = 0;
        long phone = 0;
        long parsed_number;
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
            parsed_number = parseNumber(phone, String.valueOf(code).length());
            //System.out.println(parsed_number);
            PhoneNumber number = new PhoneNumber().setCountryCode(code).setNationalNumber(parsed_number);
            PhoneNumberType numberType = phoneUtil.getNumberType(number);
            // categorize by country
            categorizeNumber(numberType, code, parsed_number);
        }

        // close 
        workbook.close();
        inputStream.close();
        //System.out.println(count+"last: code:"+code+"number:"+phone);

    }

    /**
     * categorize type, map to carrierlistmap
     *
     * @param type
     * @param code
     * @param number
     */
    public void categorizeNumber(PhoneNumberType type, int code, long number) {
        // empty map
        CountryCount obj;
        if (map.containsKey(code)) {
            obj = map.get(code);
        } else {
            //String name = codeMap.get(code);
            //if(name == null || name.equals("")) name = "unknown";
            obj = new CountryCount(code);
        }
        //map.put(code, value);
        obj.addTotal();
        switch (type) {
            case MOBILE:
            case FIXED_LINE_OR_MOBILE:
                // map carrier
                carrierBreakdown(code, number);
                obj.addMobile();
                break;
            case FIXED_LINE:
                obj.addFix();
                break;
            case UNKNOWN:
                obj.addUnknown();
                break;
            default:
                obj.addOther();
        }
        // put back to map
        map.put(code, obj);

    }

    public void printMap(Map<String, Integer> map) {
        for (Map.Entry pair : map.entrySet()) {
            //iterate over the pairs
            System.out.println(pair.getKey() + " " + pair.getValue());
        }
    }

    public void printCarrierMap() {
        Iterator it = carrierListMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            int code = (int) pair.getKey();
            String name = codeMap.get(code);
            System.out.println("Code: " + pair.getKey() + " -- " + name);
            //  + " --- " + pair.getValue());
            Map<String, Integer> tmp = (Map) pair.getValue();
            //System.out.print(tmp.isEmpty());
            tmp.entrySet().stream().forEach((entry) -> {
                String car = entry.getKey();
                if ("".equals(car)) {
                    car = "unknown";
                }
                System.out.println("carrier:" + car + ", count:" + entry.getValue());
            });
            System.out.println();
            it.remove(); // avoids a ConcurrentModificationException
        }
    }

    /**
     * parse to national number
     *
     * @param number
     * @param n
     * @return national number
     */
    public long parseNumber(long number, int n) {
        String str = String.valueOf(number);
        return Long.parseLong(str.substring(n, str.length()));
    }

    /**
     * load to codeMap
     *
     * @param sheet
     */
    public void loadCountryCode(Sheet sheet) {
        Iterator<Row> iterator = sheet.iterator();
        Row nextRow = iterator.next();
        String country;
        int code;
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

    public void addCol() throws FileNotFoundException, IOException {
        FileInputStream file = new FileInputStream(new File("Phone_Number_0829.xlsx"));
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        XSSFSheet sheet = workbook.getSheetAt(0);
        Cell cell = null;
        //Update the value of cell
        Row row = sheet.getRow(0);
        cell = row.createCell(6);
        int rownum = 1;
        cell.setCellValue("unknown % in Mobile");
        cell = row.createCell(8);
        cell.setCellValue("Portability");

        for (Map.Entry pair : map.entrySet()) {
            double unknownRatio = 0;
            int code = (int) pair.getKey();
            String region = phoneUtil.getRegionCodeForCountryCode(code);
            boolean isPortable = phoneUtil.isMobileNumberPortableRegion(region);
            int total = 0;
            Map<String, Integer> maps = carrierListMap.get(code);
            if(maps!=null){
            //boolean flag = true;
            for (int val : maps.values()) {
                total += val;
            }
            for (Map.Entry entry : maps.entrySet()) {
                String car = (String) entry.getKey();
                if ("".equals(car)) {
                    int count = (int) entry.getValue();
                    double ratio = (count + 0.0) / total * 100;
                    //flag = false;
                    unknownRatio = ((int) (ratio * 100)) / 100.0;
                    //car = "unknown";

                }
            }
            }
            row = sheet.getRow(rownum);
            cell = row.createCell(6);
            cell.setCellValue(unknownRatio);
            cell = row.createCell(8);
            cell.setCellValue(isPortable ? "Y" : "N");
            rownum++;
        }
        try {
            file.close();
            //Write the workbook in file system
            FileOutputStream out = new FileOutputStream(new File("Phone_Number_0829.xlsx"));
            workbook.write(out);
            out.close();
            System.out.println("written successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * write to sheet: carrier break down
     */
    public void carrierPercentSheet(String filename) throws FileNotFoundException, IOException {

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("CarrierCountryPercent");
        Row row = sheet.createRow(0);
        Cell firstrow = row.createCell(0);
        firstrow.setCellValue("Country code");
        firstrow = row.createCell(1);
        firstrow.setCellValue("Country name");
        firstrow = row.createCell(2);
        firstrow.setCellValue("Carrier name");
        firstrow = row.createCell(3);
        firstrow.setCellValue("Carrier percentage");
        firstrow = row.createCell(4);
        firstrow.setCellValue("Carrier count/Total");
        /**
         * for (int key : carrierListMap.keySet()){ //iterate over key
         * //System.out.println(key); String name = codeMap.get(key);
         * Map<String, Integer> newMap = carrierListMap.get(key);
         * System.out.println(newMap.isEmpty()); for (Map.Entry entry :
         * newMap.entrySet()){ String car = (String) entry.getKey(); int count =
         * (int) entry.getValue(); double ratio = (count + 0.0) / total * 100;
         * if ("".equals(car)) { car = "Unknown"; } System.out.println(ratio); }
         * }
         */
        //Iterator it = carrierListMap.entrySet().iterator();
        int rownum = 1;
        System.out.println(carrierListMap.isEmpty());
        for (Map.Entry pair : carrierListMap.entrySet()) {
            //Map.Entry pair = (Map.Entry) it.next();
            int code = (int) pair.getKey();
            //String name = codeMap.get(code);
            int total = 0;
            //System.out.println(code+" total:"+total);
            Map<String, Integer> maps = carrierListMap.get(code);
            //System.out.println(maps.isEmpty());
            for (int val : maps.values()) {
                //iterate over values
                total += val;
            }
            for (Map.Entry entry : maps.entrySet()) {
                //iterate over the pairs 
                //Map.Entry entry = (Map.Entry) it2.next();
                String car = (String) entry.getKey();
                int count = (int) entry.getValue();
                double ratio = (count + 0.0) / total * 100;
                if ("".equals(car)) {
                    car = "unknown";
                }
                System.out.println("carrier:" + car + ", ratio %:" + ratio);
                row = sheet.createRow(rownum++);
                Cell cell = row.createCell(0);
                cell.setCellValue(code);
                cell = row.createCell(1);
                cell.setCellValue(prefix.get(code));
                cell = row.createCell(2);
                cell.setCellValue(car);
                cell = row.createCell(3);
                cell.setCellValue(((int) (ratio * 100) / 100.0));
                cell = row.createCell(4);
                cell.setCellValue(count + "/" + total);
                //it2.remove();
            }
            //System.out.println();
            //it.remove(); // avoids a ConcurrentModificationException   
        }
        try {
            //Write the workbook in file system
            FileOutputStream out = new FileOutputStream(new File(filename));
            workbook.write(out);
            out.close();
            System.out.println("written successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void listCarriers() {
        for (int code : carrierListMap.keySet()) {
            //iterate over key
            System.out.print(codeMap.get(code) + ": ");
            Map<String, Integer> tmp = carrierListMap.get(code);

            for (String carrier : tmp.keySet()) {
                if (carrier.equals("")) {
                    continue;
                }
                System.out.print(carrier + ". ");
            }
            System.out.println();
        }
    }

    /**
     * write to sheet: ratio
     */
    public void toSheet() {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("typeRatioResult");
        //Set<String> keyset = data.keySet();
        Row row = sheet.createRow(0);
        Cell firstrow = row.createCell(0);
        firstrow.setCellValue("Country Code");
        firstrow = row.createCell(1);
        firstrow.setCellValue("Country Name");
        firstrow = row.createCell(2);
        firstrow.setCellValue("Total Numbers");
        firstrow = row.createCell(3);
        firstrow.setCellValue("Mobile %");
        firstrow = row.createCell(4);
        firstrow.setCellValue("Fixed Line %");
        firstrow = row.createCell(5);
        firstrow.setCellValue("Unknown %");

        Iterator it = map.entrySet().iterator();
        int rownum = 1;
        while (it.hasNext()) {
            row = sheet.createRow(rownum++);

            Map.Entry pair = (Map.Entry) it.next();
            //System.out.println(pair.getKey() + " --- " + pair.getValue().toString());
            Cell cell = row.createCell(0);
            cell.setCellValue((Integer) pair.getKey());
            cell = row.createCell(1);
            cell.setCellValue((String) ((CountryCount) pair.getValue()).countryName);
            cell = row.createCell(2);
            cell.setCellValue(((CountryCount) pair.getValue()).totalCount);
            cell = row.createCell(3);
            cell.setCellValue(((double) ((CountryCount) pair.getValue()).mobileRatio()));
            cell = row.createCell(4);
            cell.setCellValue(((double) ((CountryCount) pair.getValue()).fixRatio()));
            cell = row.createCell(5);
            cell.setCellValue(((double) ((CountryCount) pair.getValue()).unknownRatio()));
            it.remove(); // avoids a ConcurrentModificationException
        }

        try {
            //Write the workbook in file system
            FileOutputStream out = new FileOutputStream(new File("PhoneNumberResult0829.xlsx"));
            workbook.write(out);
            out.close();
            System.out.println("written successful.");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String getCarrier(int code, long number) {
        PhoneNumber pn = new PhoneNumber().setCountryCode(code).setNationalNumber(number);
        return carrierMapper.getNameForNumber(pn, Locale.ENGLISH);

    }

    public void carrierBreakdown(int code, long number) {
        String carrier = getCarrier(code, number);
        Map<String, Integer> cur = null;
        if (carrier == null || "".equals(carrier)) {
            carrierNullCount++;
        }

        // has country 
        if (carrierListMap.containsKey(code)) {
            cur = carrierListMap.get(code);
            // if carrier in map
            if (cur.containsKey(carrier)) {
                int count = cur.get(carrier);
                // increase count
                cur.put(carrier, ++count);
            } // if not in map, add to map with count 1
            else {
                cur.put(carrier, 1);
            }
            // put back to map
            carrierListMap.put(code, cur);
        } else {
            //add code to map
            cur = new HashMap<>();
            cur.put(carrier, 1);
            carrierListMap.put(code, cur);
        }
    }

    public void loadResult() throws FileNotFoundException, IOException {
        FileInputStream inputStream = new FileInputStream(new File("result.xlsx"));
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet carrierSheet = workbook.getSheetAt(1); // country - carrier name - count
        Sheet idSheet = workbook.getSheetAt(2); // id - carrier name 
        Iterator<Row> iterator = idSheet.iterator();
        Iterator<Row> iterator2 = carrierSheet.iterator();
        Row nextRow = iterator.next();
        Row nextRow2 = iterator2.next();
        while (iterator2.hasNext()) {
            nextRow2 = iterator2.next();
            Iterator<Cell> cellIterator = nextRow2.cellIterator();
            Cell cell = cellIterator.next(); //1
            int c = (int) cell.getNumericCellValue();
            cellIterator.next(); //2
            cell = cellIterator.next(); //3                      
            String name = cell.getStringCellValue();
            //System.out.print(name+" ");
            carriers.put(name, c);
        }
        //System.out.println();
        int id;
        String carrierName, countryAbrv;
        while (iterator.hasNext()) {
            nextRow = iterator.next();
            Iterator<Cell> cellIterator = nextRow.cellIterator();
            // network id 1
            Cell cell = nextRow.getCell(0);
            //Cell cell = cellIterator.next();
            id = (int) cell.getNumericCellValue();
            //cellIterator.next(); // 1
            cell = nextRow.getCell(2); // 3 carrier name 
            carrierName = cell.getStringCellValue();
            //cellIterator.next(); // 4
            cell = nextRow.getCell(4); // 5
            countryAbrv = cell.getStringCellValue();
            //System.out.println(id+"-"+carrierName+"-"+countryAbrv);
            idMap.put(id, new Carrier(carrierName, countryAbrv));
        }
        workbook.close();
        inputStream.close();
    }

    public Map<String, Integer> matchId() {
        Map<String, Integer> newIdMap = new HashMap<>();
        for (Map.Entry pair : carriers.entrySet()) {
            for (Map.Entry entry : idMap.entrySet()) {
                Carrier obj = (Carrier) entry.getValue();
                if (obj.compare(pair)) {
                    newIdMap.put((String) pair.getKey(), (int) entry.getKey());
                    System.out.println(pair.getKey() + "-" + pair.getValue() + ":" + obj.name + "-" + obj.country);
                }

            }
        }
        printMap(newIdMap);
        return newIdMap;
    }
    
    public void test(){
        //System.out.println(phoneUtil.getRegionCodeForCountryCode(86));
        //+79224004820
        //System.out.println(phoneUtil.getRegionCodeForCountryCode(256));
        PhoneNumber swissMobileNumber =
    new PhoneNumber().setCountryCode(7).setNationalNumber(9224004820L);
        System.out.println(phoneUtil.isMobileNumberPortableRegion(phoneUtil.getRegionCodeForNumber(swissMobileNumber)));
        System.out.println(phoneUtil.isMobileNumberPortableRegion(phoneUtil.getRegionCodeForCountryCode(84)));
        System.out.println(phoneUtil.isMobileNumberPortableRegion(phoneUtil.getRegionCodeForCountryCode(41)));
    }
}
