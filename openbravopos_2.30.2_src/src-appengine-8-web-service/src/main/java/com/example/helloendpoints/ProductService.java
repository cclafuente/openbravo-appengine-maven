 //[START begin]
package com.example.helloendpoints;

import java.util.List;

import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;

import mobi.nordpos.catalog.dao.ormlite.ProductPersist;
import mobi.nordpos.catalog.model.Product;
//[END begin]
//[START api_def]

/**
 * Defines v1 of a productlist API, which provides simple "greeting" methods.
 */
@Api(name = "products",
version = "v1",
scopes = {Constants.EMAIL_SCOPE},
clientIds = {Constants.WEB_CLIENT_ID, Constants.ANDROID_CLIENT_ID, Constants.IOS_CLIENT_ID},
audiences = {Constants.ANDROID_AUDIENCE}
)
public class ProductService {

	Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    ConnectionSource connection;
    
//[END api_def]
//[START productlist]
    
    @ApiMethod(name = "products.productlist", path = "products/productlist")
	List<Product> listProducts(@Named("category") Integer productCategory){
		try {
            connection = new JdbcConnectionSource("A", "B", "C");
            ProductPersist productDao = new ProductPersist(connection);
            QueryBuilder qb = productDao.queryBuilder();
            return qb.query();
		} catch (Exception e){
			logger.warn(" Execepcion buscando productos ", e);
		}
		return null;
    }
//[END productlist]
//[START insert]
    
    @ApiMethod(name = "products.insert", httpMethod = "post")
    public Product insertProduct(@Named("times") Integer times, Product product) {
      Product response = new Product();
      StringBuilder responseBuilder = new StringBuilder();
      for (int i = 0; i < times; i++) {
        responseBuilder.append(product.toString());
      }
      response.setName(responseBuilder.toString());
      return response;
    }
//[END insert]	
}
