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

import com.openbravo.pos.ticket.ProductInfo;
import com.openbravo.pos.ticket.TaxInfo;
import com.openbravo.pos.ticket.TicketInfo;
import com.openbravo.pos.ticket.TicketLineInfo;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;
import mobi.nordpos.dao.model.Product;
import mobi.nordpos.dao.model.SharedTicket;
import mobi.nordpos.dao.factory.TaxPersist;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.SimpleMessage;
import net.sourceforge.stripes.validation.SimpleError;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidateNestedProperties;
import net.sourceforge.stripes.validation.ValidationErrors;
import net.sourceforge.stripes.validation.ValidationMethod;

/**
 * @author Andrey Svininykh <svininykh@gmail.com>
 */
public class OrderProductActionBean extends OrderBaseActionBean {

    private static final String PRODUCT_ORDER = "/WEB-INF/jsp/product_order.jsp";

    @Validate(on = "add", required = true)
    BigDecimal orderUnit;

    @DefaultHandler
    public Resolution form() {
        return new ForwardResolution(PRODUCT_ORDER);
    }

    public Resolution add() {
        return new ForwardResolution(CategoryListActionBean.class);
    }

    @ValidateNestedProperties({
        @Validate(field = "code",
                required = true,
                trim = true)
    })
    @Override
    public void setProduct(Product product) {
        super.setProduct(product);
    }

    public BigDecimal getOrderUnit() {
        return orderUnit;
    }

    public void setOrderUnit(BigDecimal orderUnit) {
        this.orderUnit = orderUnit;
    }

    @ValidationMethod
    public void validateProductCodeIsAvalaible(ValidationErrors errors) {
        TaxPersist taxPersist = new TaxPersist();
        try {
            productPersist.init(getDataBaseConnection());
            taxPersist.init(getDataBaseConnection());
            Product product = productPersist.find(Product.CODE, getProduct().getCode());
            if (product != null) {
                product.setTax(taxPersist.read(product.getTaxCategory().getId()));
                setProduct(product);
            } else {
                errors.add("product.code", new SimpleError(
                        getLocalizationKey("error.CatalogNotInclude")));
            }
        } catch (SQLException ex) {
            getContext().getValidationErrors().addGlobalError(
                    new SimpleError(ex.getMessage()));
        }
    }

    @ValidationMethod(on = "add", priority = 1)
    public void tryOrderChange(ValidationErrors errors) {
        Product product = getProduct();
        ProductInfo productInfo = new ProductInfo();
        productInfo.setId(product.getId());
        productInfo.setPriceSell(product.getPriceSell().doubleValue());
        productInfo.setName(product.getName());
        productInfo.setTaxcat(product.getTaxCategory().getId());
        productInfo.setCategoryId(product.getProductCategory().getId());
        productInfo.setCom(product.getCom());

        TaxInfo taxInfo = new TaxInfo();
        taxInfo.setId(product.getTax().getId());
        taxInfo.setRate(product.getTax().getRate().doubleValue());
        taxInfo.setTaxcategoryid(product.getTaxCategory().getId());

        TicketLineInfo ticketLine = new TicketLineInfo(productInfo, product.getPriceSell().doubleValue(), taxInfo);
        ticketLine.setMultiply(orderUnit.doubleValue());

        try {
            TicketInfo ticket;
            SharedTicket sharedTicket = getContext().getOrder();
            sharedTicketPersist.init(getDataBaseConnection());
            if (sharedTicket == null) {
                ticket = new TicketInfo();
                ticket.setTickettype(TicketInfo.RECEIPT_NORMAL);
                ticket.setM_dDate(new Date());
                ticket.addLine(ticketLine);
                sharedTicket = new SharedTicket();
                sharedTicket.setId(UUID.randomUUID().toString());
                sharedTicket.setName(ticket.getName());
                sharedTicket.setContent(ticket);
                getContext().getMessages().add(
                        new SimpleMessage(getLocalizationKey("message.OrderTicketLine.added"),
                                sharedTicketPersist.add(sharedTicket).getName(), getProduct().getName(), getOrderUnit())
                );
            } else {
                ticket = sharedTicket.getContent();
                ticket.addLine(ticketLine);
                sharedTicket.setContent(ticket);
                if (sharedTicketPersist.change(sharedTicket)) {
                    getContext().getMessages().add(
                            new SimpleMessage(getLocalizationKey("message.OrderTicketLine.added"),
                                    sharedTicket.getName(), getProduct().getName(), getOrderUnit()));
                }
            }
            getContext().setOrder(sharedTicket);
        } catch (SQLException ex) {
            getContext().getValidationErrors().addGlobalError(
                    new SimpleError(ex.getMessage()));
        }
    }

}
