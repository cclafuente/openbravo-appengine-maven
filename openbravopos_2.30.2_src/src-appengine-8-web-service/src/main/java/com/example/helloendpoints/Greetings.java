
 //[START begin]
package com.example.helloendpoints;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Named;

import mobi.nordpos.catalog.dao.ormlite.ProductCategoryPersist;
import mobi.nordpos.catalog.dao.ormlite.ProductPersist;
import mobi.nordpos.catalog.dao.ormlite.TaxPersist;
import mobi.nordpos.catalog.model.Product;
import mobi.nordpos.catalog.model.ProductCategory;
import mobi.nordpos.catalog.model.Tax;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.users.User;
//[END begin]
//[START api_def]
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.openbravo.pos.sales.TaxesLogic;

/**
 * Defines v1 of a helloworld API, which provides simple "greeting" methods.
 */
@Api(name = "helloworld",
    version = "v1",
    scopes = {Constants.EMAIL_SCOPE},
    clientIds = {Constants.WEB_CLIENT_ID, Constants.ANDROID_CLIENT_ID, Constants.IOS_CLIENT_ID},
    audiences = {Constants.ANDROID_AUDIENCE}
)
public class Greetings {

  public static ArrayList<HelloGreeting> greetings = new ArrayList<HelloGreeting>();

  Logger logger = LoggerFactory.getLogger(this.getClass().getName());

  ConnectionSource connection;
  
  String categoryPan = "28fa139e-7064-11e6-b1e0-62b567e03155";
  String categoryDoces = "94d49e89-352b-40af-bdd9-18db7fd670c6";
  
  private String getDataBaseURL(){ return "jdbc:mysql://207.223.167.6/openbravopos2"; }
  private String getDataBaseUser(){ return "testuser"; }
  private String getDataBasePassword(){ return "testpass"; }
  
  
  static {
    greetings.add(new HelloGreeting("hello world!"));
    greetings.add(new HelloGreeting("goodbye world!"));
  }
//[END api_def]
//[START getgreetings]

  public HelloGreeting getGreeting(@Named("id") Integer id) throws NotFoundException {
    try {
      return greetings.get(id);
    } catch (IndexOutOfBoundsException e) {
      throw new NotFoundException("Greeting not found with an index: " + id);
    }
  }

  public ArrayList<HelloGreeting> listGreeting() {
    return greetings;
  }
//[END getgreetings]
//[START multiplygreetings]

  @ApiMethod(name = "greetings.multiply", httpMethod = "post")
  public HelloGreeting insertGreeting(@Named("times") Integer times, HelloGreeting greeting) {
    HelloGreeting response = new HelloGreeting();
    StringBuilder responseBuilder = new StringBuilder();
    for (int i = 0; i < times; i++) {
      responseBuilder.append(greeting.getMessage());
    }
    response.setMessage(responseBuilder.toString());
    return response;
  }
//[END multiplygreetings]
//[START auth] 

  @ApiMethod(name = "greetings.authed", path = "hellogreeting/authed")
  public HelloGreeting authedGreeting(User user) {
    HelloGreeting response = new HelloGreeting("hello " + user.getEmail());
    return response;
  }
//[END auth]
//[START productlist]
  
  @ApiMethod(name = "greetings.productlist", path = "hellogreeting/productlist")
  public List<Product> getProductList(@Named("categroy") String categoryId) {
	  List<Product> products = new ArrayList<Product>();
	  try{
	  ProductCategory category = readProductCategory(categoryId);
      if (category != null) {
          products = category.getProductList();
          for (Product product : products) {
              product.setTax(readTax(product.getTaxCategory().getId()));
          }
      }
	  }catch (Exception e){
		  logger.error(" Error buscando productos ", e);
	  }
      return products;
        
  }
//[END productlist]
  
  
  protected ProductCategory readProductCategory(String id) throws SQLException {
      try {
          connection = new JdbcConnectionSource("jdbc:mysql://207.223.167.6/openbravopos2", "testuser", "testpass");
          ProductCategoryPersist productCategoryDao = new ProductCategoryPersist(connection);
          return productCategoryDao.queryForId(id);
      } finally {
          if (connection != null) {
              connection.close();
          }
      }
  }
  
  
  protected Tax readTax(String taxCategoryId) throws SQLException {
      try {
          connection = new JdbcConnectionSource(getDataBaseURL(), getDataBaseUser(), getDataBasePassword());
          TaxPersist taxDao = new TaxPersist(connection);
          TaxesLogic taxLogic = new TaxesLogic(taxDao.queryForAll());
          return taxLogic.getTax(taxCategoryId, new Date());
      } finally {
          if (connection != null) {
              connection.close();
          }
      }
  }
  
}
