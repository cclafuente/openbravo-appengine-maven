/**
 * Copyright (c) 2012-2015 Nord Trading Network.
 *
 * http://www.nordpos.mobi
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package mobi.nordpos.dao.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.openbravo.pos.util.Hashcypher;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;
import java.util.UUID;

/**
 * @author cclafuente@gmail.com
 */
@DatabaseTable(tableName = "CUSTOMERS")
public class Customer implements Serializable {

    private static final long serialVersionUID = 1015005824491883669L;
	
    public static final String ID = "ID";
    public static final String NAME = "NAME";
    public static final String SEARCHKEY = "SEARCHKEY";
    public static final String TAXID = "TAXID";
    public static final String TAXCATEGORY = "TAXCATEGORY";
    public static final String EMAIL = "EMAIL";
    public static final String PROPERTIES = "PROPERTIES";
    public static final String CARD = "CARD";
    public static final String LOCATION_COUNTRY = "COUNTRY";
    public static final String LOCATION_REGION = "REGION";
    public static final String LOCATION_CITY = "CITY";
    public static final String LOCATION_ADDR_STR1 = "ADDRESS";
    public static final String LOCATION_ADDR_STR2 = "ADDRESS2";
    public static final String LOCATION_POSTAL_CODE = "POSTAL";
    public static final String LOCATION_PHONE = "PHONE";
    public static final String LOCATION_FAX = "FAX";
    public static final String PERSON_FIRSTNAME = "FIRSTNAME";
    public static final String PERSON_LASTNAME = "LASTNAME";
    public static final String PERSON_PHONE = "PHONE2";
    public static final String NOTES = "NOTES";

    private static final String LOGIN_PASSWORD_KEY = "customer.login.password";

    @DatabaseField(generatedId = true, columnName = ID)
    private UUID id;

    @DatabaseField(columnName = NAME, canBeNull = false)
    private String name;

    @DatabaseField(columnName = SEARCHKEY, canBeNull = false)
    private String searchKey;

    @DatabaseField(columnName = TAXID, canBeNull = true)
    private String taxpayerId;

    @DatabaseField(foreign = true,
            columnName = TAXCATEGORY,
            foreignColumnName = TaxCategory.ID,
            foreignAutoRefresh = true,
            canBeNull = true)
    private TaxCategory taxCategory;

    @DatabaseField(columnName = EMAIL, canBeNull = true)
    private String email;

    @DatabaseField(columnName = PROPERTIES, dataType = DataType.BYTE_ARRAY)
    byte[] properties;

    @DatabaseField(columnName = CARD, canBeNull = true)
    private String card;

    @DatabaseField(columnName = LOCATION_COUNTRY, canBeNull = true)
    private String locationCountry;

    @DatabaseField(columnName = LOCATION_REGION, canBeNull = true)
    private String locationRegion;

    @DatabaseField(columnName = LOCATION_CITY, canBeNull = true)
    private String locationCity;

    @DatabaseField(columnName = LOCATION_ADDR_STR1, canBeNull = true)
    private String locationAddressStr1;

    @DatabaseField(columnName = LOCATION_ADDR_STR2, canBeNull = true)
    private String locationAddressStr2;

    @DatabaseField(columnName = LOCATION_POSTAL_CODE, canBeNull = true)
    private String locationPostalCode;

    @DatabaseField(columnName = LOCATION_PHONE, canBeNull = true)
    private String locationPhone;

    @DatabaseField(columnName = LOCATION_FAX, canBeNull = true)
    private String locationFax;

    @DatabaseField(columnName = PERSON_FIRSTNAME, canBeNull = true)
    private String personFirstname;

    @DatabaseField(columnName = PERSON_LASTNAME, canBeNull = true)
    private String personLastname;

    @DatabaseField(columnName = PERSON_PHONE, canBeNull = true)
    private String personPhone;

    @DatabaseField(columnName = NOTES, canBeNull = true)
    private String notes;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSearchKey() {
        return searchKey;
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }

    public String getTaxpayerId() {
        return taxpayerId;
    }

    public void setTaxpayerId(String taxpayerId) {
        this.taxpayerId = taxpayerId;
    }

    public TaxCategory getTaxCategory() {
        return taxCategory;
    }

    public void setTaxCategory(TaxCategory taxCategory) {
        this.taxCategory = taxCategory;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }

    public String getLocationCountry() {
        return locationCountry;
    }

    public void setLocationCountry(String locationCountry) {
        this.locationCountry = locationCountry;
    }

    public String getLocationRegion() {
        return locationRegion;
    }

    public void setLocationRegion(String locationRegion) {
        this.locationRegion = locationRegion;
    }

    public String getLocationCity() {
        return locationCity;
    }

    public void setLocationCity(String locationCity) {
        this.locationCity = locationCity;
    }

    public String getLocationAddressStr1() {
        return locationAddressStr1;
    }

    public void setLocationAddressStr1(String locationAddressStr1) {
        this.locationAddressStr1 = locationAddressStr1;
    }

    public String getLocationAddressStr2() {
        return locationAddressStr2;
    }

    public void setLocationAddressStr2(String locationAddressStr2) {
        this.locationAddressStr2 = locationAddressStr2;
    }

    public String getLocationPostalCode() {
        return locationPostalCode;
    }

    public void setLocationPostalCode(String locationPostalCode) {
        this.locationPostalCode = locationPostalCode;
    }

    public String getLocationPhone() {
        return locationPhone;
    }

    public void setLocationPhone(String locationPhone) {
        this.locationPhone = locationPhone;
    }

    public String getLocationFax() {
        return locationFax;
    }

    public void setLocationFax(String locationFax) {
        this.locationFax = locationFax;
    }

    public String getPersonFirstname() {
        return personFirstname;
    }

    public void setPersonFirstname(String personFirstname) {
        this.personFirstname = personFirstname;
    }

    public String getPersonLastname() {
        return personLastname;
    }

    public void setPersonLastname(String personLastname) {
        this.personLastname = personLastname;
    }

    public String getPersonPhone() {
        return personPhone;
    }

    public void setPersonPhone(String personPhone) {
        this.personPhone = personPhone;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Properties getProperties() throws IOException {
        Properties p = new Properties();
        if (this.properties != null) {
            p.loadFromXML(new ByteArrayInputStream(this.properties));
        }
        return p;
    }

    public void setProperties(Properties p) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        p.storeToXML(outputStream, "Customer properties", "UTF-8");
        this.properties = outputStream.toByteArray();
    }

    public String getPassword() throws IOException {
        return getProperties().getProperty(LOGIN_PASSWORD_KEY);
    }

    public void setPassword(String password) throws UnsupportedEncodingException, NoSuchAlgorithmException, IOException {
        Properties p = getProperties();
        p.setProperty(LOGIN_PASSWORD_KEY, Hashcypher.hashString(password));
        setProperties(p);
    }

    public boolean isAuthentication(String password) throws UnsupportedEncodingException, NoSuchAlgorithmException, IOException {
        return Hashcypher.authenticate(password, getPassword());
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || other.getClass() != getClass()) {
            return false;
        }
        return name.equals(((Customer) other).name);
    }

}
