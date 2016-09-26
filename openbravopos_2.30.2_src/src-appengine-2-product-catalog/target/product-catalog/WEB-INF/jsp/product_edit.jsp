<%--
    Document   : product_edit
    Author     : Andrey Svininykh (svininykh@gmail.com)
    Copyright  : Nord Trading Network
    License    : Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
--%>

<%@include file="/WEB-INF/jsp/common/taglibs.jsp"%>
<stripes:layout-render name="/WEB-INF/jsp/common/layout_main.jsp"
                       title="Product Edit"
                       pageid="ProductEdit">

    <stripes:layout-component name="buttons_left">
        <sdynattr:link href="/Welcome.action"
                       class="ui-btn ui-corner-all ui-icon-home ui-btn-icon-notext">
            <fmt:message key="label.home" />
        </sdynattr:link>          
        <sdynattr:link href="/CategoryProductList.action"
                       class="ui-btn ui-corner-all ui-icon-bars ui-btn-icon-left">
            <stripes:param name="category.id" value="${actionBean.product.productCategory.id}"/>
            <c:out value="${actionBean.product.productCategory.name}"/>
        </sdynattr:link>           
    </stripes:layout-component>

    <stripes:layout-component name="title">
        <fmt:message key="label.ProductEdit" />
    </stripes:layout-component>

    <stripes:layout-component name="buttons_right">
        <a href="#delete_product" 
           data-rel="popup" 
           data-position-to="window" 
           data-transition="pop" 
           class="ui-btn ui-corner-all ui-icon-delete ui-btn-icon-left ui-btn-b ui-shadow">
            <fmt:message key="label.delete" />
        </a>
        <div data-role="popup" 
             id="delete_product" 
             data-overlay-theme="b" data-theme="b" 
             data-dismissible="false" style="max-width:400px;">
            <div data-role="header" data-theme="a">
                <h1><fmt:message key="label.dialog.delete" /></h1>
            </div>
            <div role="main" class="ui-content">
                <h3 class="ui-title">
                    <c:out value="${actionBean.product.name}"/>
                </h3>
                <p><fmt:message key="label.ask.delete" /></p>
                <fieldset class="ui-grid-a">
                    <div class="ui-block-a">
                        <a href="#" 
                           class="ui-btn ui-corner-all ui-icon-forbidden ui-btn-icon-left ui-btn-b ui-shadow" 
                           data-rel="back" 
                           data-transition="flow">
                            <fmt:message key="no" />
                        </a>   
                    </div>
                    <div class="ui-block-b">
                        <stripes:form action="/ProductChange.action?delete">
                            <div>
                                <stripes:hidden name="product.id" value="${actionBean.product.id}"/>
                                <stripes:hidden name="product.name" value="${actionBean.product.name}"/>
                            </div>
                            <sdynattr:submit name="yes" data-theme="a" data-icon="check"/>                    
                        </stripes:form>
                    </div>
                </fieldset>
            </div>
        </div>        
    </stripes:layout-component>

    <stripes:layout-component name="content">
        <sdynattr:form action="/ProductChange.action" data-ajax="false">
            <div>
                <stripes:hidden name="product.id" value="${actionBean.product.id}"/>
                <stripes:hidden name="product.taxCategory.id" value="${actionBean.product.taxCategory.id}"/>
                <stripes:hidden name="product.tax.rate" value="${actionBean.product.tax.rate}"/>
                <stripes:hidden name="product.productCategory.id" value="${actionBean.product.productCategory.id}"/>
                <stripes:hidden name="product.productCategory.name" value="${actionBean.product.productCategory.name}"/>
                <stripes:hidden name="currentProduct.name" value="${actionBean.product.name}"/>
                <stripes:hidden name="currentProduct.code" value="${actionBean.product.code}"/>
                <stripes:hidden name="currentProduct.reference" value="${actionBean.product.reference}"/>
            </div>
            <ul data-role="listview" data-inset="true">  
                <li class="ui-field-contain">
                    <stripes:label name="label.Product.name" for="productName" />
                    <input name="product.name" id="productName" type="text"
                           placeholder="<fmt:message key='label.ProductName.enter' />" 
                           value="${actionBean.product.name}"
                           data-clear-btn="true">
                </li>
                <li class="ui-field-contain">
                    <stripes:label name="label.Product.reference" for="productReference" />
                    <input name="product.reference" id="productReference" type="text"
                           placeholder="<fmt:message key='label.ProductReference.enter' />"
                           value="${actionBean.product.reference}"
                           data-clear-btn="true">
                </li>                
                <li class="ui-field-contain">
                    <stripes:label name="label.Product.code" for="productCode" />
                    <input name="product.code" id="productCode" type="text"
                           placeholder="<fmt:message key='label.ProductCode.enter' />"
                           value="${actionBean.product.code}"
                           data-clear-btn="true">
                </li>
                <li class="ui-field-contain">
                    <stripes:label name="label.Product.priceBuy" for="productBuyPrice"/>
                    <input name="product.priceBuy" id="productBuyPrice" type="number"
                           placeholder="<fmt:message key='label.ProductBuyPrice.enter' />"
                           step="0.01"
                           value="<fmt:formatNumber value="${actionBean.product.priceBuy}"
                                          type="NUMBER"
                                          pattern="#0.00"
                                          maxFractionDigits="2" 
                                          minFractionDigits="2"/>"
                           data-clear-btn="true">
                </li>                
                <li class="ui-field-contain">
                    <stripes:label name="label.Product.taxPriceSell" for="productSellPrice"/>
                    <input name="product.taxPriceSell" id="productSellPrice" type="number"
                           placeholder="<fmt:message key='label.ProductSellPrice.enter' />"
                           step="0.01"
                           value="<fmt:formatNumber value="${actionBean.product.taxPriceSell}"
                                          type="NUMBER"
                                          pattern="#0.00"
                                          maxFractionDigits="2" 
                                          minFractionDigits="2"/>"
                           data-clear-btn="true">
                </li>
                <li class="ui-field-contain">
                    <stripes:label name="label.ProductImage.file" for="productImageFile" />                    
                    <stripes:file name="imageFile" id="productImageFile" /> 
                </li>
                <li class="ui-body ui-body-b">
                    <fieldset class="ui-grid-a">
                        <div class="ui-block-a">
                            <sdynattr:reset name="clear" data-theme="b"/>                            
                        </div>
                        <div class="ui-block-b">
                            <sdynattr:submit name="update" data-theme="a"/>
                        </div>
                    </fieldset>
                </li>
            </ul>
        </sdynattr:form>

    </stripes:layout-component>

    <stripes:layout-component name="footer">

    </stripes:layout-component>
</stripes:layout-render>