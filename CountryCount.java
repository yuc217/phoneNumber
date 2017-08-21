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
public class CountryCount {

    String countryName;
    int countryCode;
    int totalCount,mobileCount,fixCount,unknownCount,other;
    
    
    CountryCount(int countryCode, String countryName){
        this.countryCode = countryCode;
        this.countryName = countryName;
        totalCount=0;
        mobileCount=0;
        fixCount=0;
        unknownCount=0;
        other=0;
    }
    
    public void addTotal(){
        totalCount++;
    }
    public void addMobile(){
        mobileCount++;
    }
    public void addFix(){
        fixCount++;
    }
    public void addUnknown(){
        unknownCount++;
    }
    public void addOther(){
        other++;
    }
    public String toString(){
        return countryName+" | Total:"+totalCount+" | mobile:"+mobileCount+" | fix:"+fixCount+" | unknown:"
                +unknownCount+" | other:"+other;
    } 

}