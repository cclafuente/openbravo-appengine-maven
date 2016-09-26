/**
 * Copyright (c) 2012-2014 Nord Trading Network.
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
package mobi.nordpos.catalog.action;

import java.sql.SQLException;
import mobi.nordpos.catalog.ext.Public;
import mobi.nordpos.catalog.model.Product;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.validation.SimpleError;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidateNestedProperties;
import net.sourceforge.stripes.validation.ValidationErrors;
import net.sourceforge.stripes.validation.ValidationMethod;

/**
 * @author Andrey Svininykh <svininykh@gmail.com>
 */
@Public
public class ProductViewActionBean extends ProductBaseActionBean {

    private static final String PRODUCT_VIEW = "/WEB-INF/jsp/product_view.jsp";

    @DefaultHandler
    public Resolution content() {
        return new ForwardResolution(PRODUCT_VIEW);
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

    @ValidationMethod
    public void validateProductCodeIsAvalaible(ValidationErrors errors) {
        try {
            Product product = readProduct(Product.CODE, getProduct().getCode());            
            if (product != null) {
                product.setTax(readTax(product.getTaxCategory().getId()));
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

}
