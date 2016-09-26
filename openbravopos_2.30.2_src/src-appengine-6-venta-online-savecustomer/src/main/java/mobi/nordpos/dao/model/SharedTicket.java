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

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.openbravo.pos.ticket.TicketLineInfo;

/**
 * @author Andrey Svininykh <svininykh@gmail.com>
 */
@DatabaseTable(tableName = "SHAREDTICKETS")
public class SharedTicket implements Serializable{

    private static final long serialVersionUID = -1021696216335699065L;
	
    public static final String ID = "ID";
    public static final String NAME = "NAME";
    public static final String CONTENT = "CONTENT";

    @DatabaseField(id = true, columnName = ID)
    private String id;

    @DatabaseField(columnName = NAME, unique = true, canBeNull = false)
    private String name;

    @DatabaseField(columnName = CONTENT, dataType = DataType.SERIALIZABLE, canBeNull = true)
    private com.openbravo.pos.ticket.TicketInfo content;

    @DatabaseField(persisted = false)
    private BigDecimal totalValue;

    @DatabaseField(persisted = false)
    private BigDecimal totalUnit;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public com.openbravo.pos.ticket.TicketInfo getContent() {
        return content;
    }

    public void setContent(com.openbravo.pos.ticket.TicketInfo content) {
        this.content = content;
    }

    public BigDecimal getTotalValue() {
        totalValue = BigDecimal.ZERO;
        if (content != null) {
            for (TicketLineInfo line : content.getM_aLines()) {
                totalValue = totalValue.add(BigDecimal.valueOf(line.getValue()));
            }
        }
        return totalValue.setScale(2, RoundingMode.HALF_DOWN);
    }

    public BigDecimal getTotalUnit() {
        totalUnit = BigDecimal.ZERO;
        if (content != null) {
            for (TicketLineInfo line : content.getM_aLines()) {
                totalUnit = totalUnit.add(BigDecimal.valueOf(line.getMultiply()));
            }
        }
        return totalUnit;
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
        return name.equals(((SharedTicket) other).name);
    }

}
