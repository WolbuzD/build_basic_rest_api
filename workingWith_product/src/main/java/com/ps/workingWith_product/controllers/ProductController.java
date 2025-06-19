package com.ps.workingWith_product.controllers;

import com.ps.workingWith_product.data.ProductDao;
import com.ps.workingWith_product.models.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@CrossOrigin(origins = "*") // Enable CORS if needed
public class ProductController {

    private final ProductDao productDao;

    @Autowired
    public ProductController(ProductDao productDao) {
        this.productDao = productDao;
    }


    @GetMapping("")
    public ResponseEntity<List<Product>> getAllProducts() {
        try {
            List<Product> products = productDao.getAll();
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable int id) {
        try {
            Product product = productDao.getById(id);
            if (product != null) {
                return ResponseEntity.ok(product);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PostMapping("")
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        try {
            // Basic validation
            if (product.getProductName() == null || product.getProductName().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            if (product.getUnitPrice() < 0) {
                return ResponseEntity.badRequest().build();
            }

            Product createdProduct = productDao.create(product);
            if (createdProduct != null) {
                return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<Void> updateProduct(@PathVariable int id, @RequestBody Product product) {
        try {
            // Basic validation
            if (product.getProductName() == null || product.getProductName().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            if (product.getUnitPrice() < 0) {
                return ResponseEntity.badRequest().build();
            }

            // Check if product exists
            Product existingProduct = productDao.getById(id);
            if (existingProduct == null) {
                return ResponseEntity.notFound().build();
            }

            productDao.update(id, product);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable int id) {
        try {
            // Check if product exists
            Product existingProduct = productDao.getById(id);
            if (existingProduct == null) {
                return ResponseEntity.notFound().build();
            }

            productDao.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}