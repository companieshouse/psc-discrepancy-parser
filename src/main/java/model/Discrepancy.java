package model;

import com.opencsv.bean.CsvBindByName;

public class Discrepancy {
    @CsvBindByName(column = "company_number")
    private String companyNumber;

    @CsvBindByName(column = "company_name")
    private String companyName;

    public String getCompanyNumber() {
        return companyNumber;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}
