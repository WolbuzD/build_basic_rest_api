package com.ps.workingWith_product.data.mysql;

import com.ps.workingWith_product.data.ProductDao;
import com.ps.workingWith_product.models.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlProductDao implements ProductDao {

    private final DataSource dataSource;

    @Autowired
    public MySqlProductDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Product> getAll() {
        List<Product> products = new ArrayList<>();
        String query = "SELECT ProductId, ProductName, UnitPrice FROM products ORDER BY ProductId";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                Product product = parseProduct(resultSet);
                products.add(product);
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving all products: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to retrieve products from database", e);
        }

        return products;
    }

    @Override
    public Product getById(int id) {
        String query = "SELECT ProductId, ProductName, UnitPrice FROM products WHERE ProductId = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return parseProduct(resultSet);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving product with ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to retrieve product from database", e);
        }

        return null;
    }

    @Override
    public Product create(Product product) {
        String query = "INSERT INTO products (ProductName, UnitPrice) VALUES (?, ?)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, product.getProductName());
            preparedStatement.setDouble(2, product.getUnitPrice());

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedProductId = generatedKeys.getInt(1);
                        return getById(generatedProductId);
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Error creating product: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to create product in database", e);
        }

        return null;
    }

    @Override
    public void update(int id, Product product) {
        String query = "UPDATE products SET ProductName = ?, UnitPrice = ? WHERE ProductId = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, product.getProductName());
            preparedStatement.setDouble(2, product.getUnitPrice());
            preparedStatement.setInt(3, id);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected == 0) {
                System.out.println("No product found with ID: " + id);
            }

        } catch (SQLException e) {
            System.err.println("Error updating product with ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to update product in database", e);
        }
    }

    @Override
    public void delete(int id) {
        String query = "DELETE FROM products WHERE ProductId = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, id);
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected == 0) {
                System.out.println("No product found with ID: " + id);
            }

        } catch (SQLException e) {
            System.err.println("Error deleting product with ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to delete product from database", e);
        }
    }

    private Product parseProduct(ResultSet resultSet) throws SQLException {
        int productId = resultSet.getInt("ProductId");
        String productName = resultSet.getString("ProductName");
        double unitPrice = resultSet.getDouble("UnitPrice");
        return new Product(productId, productName, unitPrice);
    }
}