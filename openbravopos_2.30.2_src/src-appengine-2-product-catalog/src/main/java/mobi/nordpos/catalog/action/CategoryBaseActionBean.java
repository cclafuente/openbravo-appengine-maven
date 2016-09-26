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

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;
import java.sql.SQLException;
import java.util.List;
import mobi.nordpos.catalog.dao.ormlite.ProductCategoryPersist;
import mobi.nordpos.catalog.model.ProductCategory;

/**
 * @author Andrey Svininykh <svininykh@gmail.com>
 */
public abstract class CategoryBaseActionBean extends BaseActionBean {

    private ProductCategory category;

    public ProductCategory getCategory() {
        return category;
    }

    public void setCategory(ProductCategory category) {
        this.category = category;
    }

    protected List<ProductCategory> readCategoryList() throws SQLException {
        try {
            connection = new JdbcConnectionSource(getDataBaseURL(), getDataBaseUser(), getDataBasePassword());
            ProductCategoryPersist productCategoryDao = new ProductCategoryPersist(connection);
            return productCategoryDao.getList();
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    protected ProductCategory readProductCategory(String column, String value) throws SQLException {
        try {
            connection = new JdbcConnectionSource(getDataBaseURL(), getDataBaseUser(), getDataBasePassword());
            ProductCategoryPersist productCategoryDao = new ProductCategoryPersist(connection);
            QueryBuilder qb = productCategoryDao.queryBuilder();
            qb.where().like(column, value);
            return (ProductCategory) qb.queryForFirst();
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    protected ProductCategory createProductCategory(ProductCategory category) throws SQLException {
        try {
            connection = new JdbcConnectionSource(getDataBaseURL(), getDataBaseUser(), getDataBasePassword());
            ProductCategoryPersist productCategoryDao = new ProductCategoryPersist(connection);
            return productCategoryDao.createIfNotExists(category);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    protected Boolean updateProductCategory(ProductCategory category) throws SQLException {
        try {
            connection = new JdbcConnectionSource(getDataBaseURL(), getDataBaseUser(), getDataBasePassword());
            ProductCategoryPersist productCategoryDao = new ProductCategoryPersist(connection);
            return productCategoryDao.update(category) > 0;
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    protected Boolean deleteProductCategory(String id) throws SQLException {
        try {
            connection = new JdbcConnectionSource(getDataBaseURL(), getDataBaseUser(), getDataBasePassword());
            ProductCategoryPersist productCategoryDao = new ProductCategoryPersist(connection);
            return productCategoryDao.deleteById(id) > 0;
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

}
