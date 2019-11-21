package model;

public class PscDiscrepancySurveyObligedEntity {
    private String companyName;
    private String obligedEntityType;
    private String contactName;
    private String contactEmail;
    private String contactPhone;
    private String contactAddressLine1;
    private String contactAddressLine2;
    private String contactAddressLine3;
    private String contactAddressLine4;
    private String contactAddressLine5;
    private String contactAddressLine6;
    private String contactAddressPostCode;

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getObligedEntityType() {
        return obligedEntityType;
    }

    public void setObligedEntityType(String obligedEntityType) {
        this.obligedEntityType = obligedEntityType;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactAddressLine1() {
        return contactAddressLine1;
    }

    public void setContactAddressLine1(String contactAddressLine1) {
        this.contactAddressLine1 = contactAddressLine1;
    }

    public String getContactAddressLine2() {
        return contactAddressLine2;
    }

    public void setContactAddressLine2(String contactAddressLine2) {
        this.contactAddressLine2 = contactAddressLine2;
    }

    public String getContactAddressLine3() {
        return contactAddressLine3;
    }

    public void setContactAddressLine3(String contactAddressLine3) {
        this.contactAddressLine3 = contactAddressLine3;
    }

    public String getContactAddressLine4() {
        return contactAddressLine4;
    }

    public void setContactAddressLine4(String contactAddressLine4) {
        this.contactAddressLine4 = contactAddressLine4;
    }

    public String getContactAddressLine5() {
        return contactAddressLine5;
    }

    public void setContactAddressLine5(String contactAddressLine5) {
        this.contactAddressLine5 = contactAddressLine5;
    }

    public String getContactAddressLine6() {
        return contactAddressLine6;
    }

    public void setContactAddressLine6(String contactAddressLine6) {
        this.contactAddressLine6 = contactAddressLine6;
    }

    public String getContactAddressPostCode() {
        return contactAddressPostCode;
    }

    public void setContactAddressPostCode(String contactAddressPostCode) {
        this.contactAddressPostCode = contactAddressPostCode;
    }

    private StringBuilder addCommaIfAlreadyAdded(boolean alreadyAdded, StringBuilder sb) {
        if (alreadyAdded) {
            sb.append(", ");
        }
        return sb;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ObligedEntity [");
        boolean alreadyAdded = false;
        if (companyName != null) {
            sb.append("companyName=").append(companyName);
            alreadyAdded = true;
        }
        if (obligedEntityType != null) {
            addCommaIfAlreadyAdded(alreadyAdded, sb);
            sb.append("obligedEntityType=").append(obligedEntityType);
            alreadyAdded = true;
        }
        if (contactName != null) {
            addCommaIfAlreadyAdded(alreadyAdded, sb);
            sb.append("contactName=").append(contactName);
            alreadyAdded = true;
        }
        if (contactEmail != null) {
            addCommaIfAlreadyAdded(alreadyAdded, sb);
            sb.append("contactEmail=").append(contactEmail);
            alreadyAdded = true;
        }
        if (contactPhone != null) {
            addCommaIfAlreadyAdded(alreadyAdded, sb);
            sb.append("contactPhone=").append(contactPhone);
            alreadyAdded = true;
        }
        if (contactAddressLine1 != null) {
            addCommaIfAlreadyAdded(alreadyAdded, sb);
            sb.append("contactAddressLine1=").append(contactAddressLine1);
            alreadyAdded = true;
        }
        if (contactAddressLine2 != null) {
            addCommaIfAlreadyAdded(alreadyAdded, sb);
            sb.append("contactAddressLine2=").append(contactAddressLine2);
            alreadyAdded = true;
        }
        if (contactAddressLine3 != null) {
            addCommaIfAlreadyAdded(alreadyAdded, sb);
            sb.append("contactAddressLine3=").append(contactAddressLine3);
            alreadyAdded = true;
        }
        if (contactAddressLine4 != null) {
            addCommaIfAlreadyAdded(alreadyAdded, sb);
            sb.append("contactAddressLine4=").append(contactAddressLine4);
            alreadyAdded = true;
        }
        if (contactAddressLine5 != null) {
            addCommaIfAlreadyAdded(alreadyAdded, sb);
            sb.append("contactAddressLine5=").append(contactAddressLine5);
            alreadyAdded = true;
        }
        if (contactAddressLine6 != null) {
            addCommaIfAlreadyAdded(alreadyAdded, sb);
            sb.append("contactAddressLine6=").append(contactAddressLine6);
            alreadyAdded = true;
        }
        if (contactAddressPostCode != null) {
            addCommaIfAlreadyAdded(alreadyAdded, sb);
            sb.append("contactAddressPostCode=").append(contactAddressPostCode);
        }
        sb.append("]");
        return sb.toString();
    }
}
