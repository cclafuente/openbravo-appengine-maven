/**
 * Copyright (c) 2012-2015 Nord Trading Network.
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
package mobi.nordpos.retail.action;

import com.openbravo.pos.ticket.TicketInfo;
import com.openbravo.pos.ticket.TicketLineInfo;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import mobi.nordpos.dao.model.SharedTicket;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.SimpleMessage;
import net.sourceforge.stripes.validation.SimpleError;
import net.sourceforge.stripes.validation.Validate;

/**
 * @author Andrey Svininykh <svininykh@gmail.com>
 */
public class OrderOpenActionBean extends OrderBaseActionBean {

    private static final String ORDER_VIEW = "/WEB-INF/jsp/order_view.jsp";

    @Validate(on = "remove", required = true)
    Integer removeLineNumber;

    @DefaultHandler
    public Resolution view() {
        return new ForwardResolution(ORDER_VIEW);
    }

    public Resolution remove() {
        TicketLineInfo removeLine = new TicketLineInfo();
        SharedTicket sharedTicket = getContext().getOrder();
        TicketInfo ticket = sharedTicket.getContent();
        List<TicketLineInfo> lines = ticket.getLines();
        ticket.setM_aLines(new ArrayList<TicketLineInfo>());
        for (TicketLineInfo line : lines) {
            if (line.getM_iLine() != removeLineNumber) {
                ticket.addLine(line);
            } else {
                removeLine = line;
            }
        }
        sharedTicket.setContent(ticket);
        try {
            sharedTicketPersist.init(getDataBaseConnection());
            if (sharedTicketPersist.change(sharedTicket)) {
                getContext().getMessages().add(
                        new SimpleMessage(getLocalizationKey("message.OrderTicketLine.removed"),
                                sharedTicket.getName(), removeLine.getAttributes().getProperty("product.name"), removeLine.getMultiply()));
            }
        } catch (SQLException ex) {
            getContext().getValidationErrors().addGlobalError(
                    new SimpleError(ex.getMessage()));
            return getContext().getSourcePageResolution();
        }
        getContext().setOrder(sharedTicket);        
        return new ForwardResolution(OrderOpenActionBean.class, "view");
    }

    public Resolution delete() throws SQLException {
        try {
            sharedTicketPersist.init(getDataBaseConnection());
            if (sharedTicketPersist.delete(getContext().getOrder().getId())) {
                getContext().getMessages().add(
                        new SimpleMessage(getLocalizationKey("message.Order.deleted")));
            }
        } catch (SQLException ex) {
            getContext().getValidationErrors().addGlobalError(
                    new SimpleError(ex.getMessage()));
            return getContext().getSourcePageResolution();
        }
        getContext().setOrder(null);
        return new ForwardResolution(CategoryListActionBean.class);
    }

    public Integer getRemoveLineNumber() {
        return removeLineNumber;
    }

    public void setRemoveLineNumber(Integer removeLineNumber) {
        this.removeLineNumber = removeLineNumber;
    }

}
