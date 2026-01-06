package com.br.deliveryapi.service;

import com.br.deliveryapi.entity.Product;
import com.br.deliveryapi.exception.ProductAlreadyExistsException;
import com.br.deliveryapi.exception.UserAlreadyExistsException;
import com.br.deliveryapi.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    ProductRepository productRepository;

    public Product create(Product product) {
        if (productRepository.existsByName(product.getName())) {
            throw new ProductAlreadyExistsException("Product Already Exists");
        }

        return productRepository.save(product);
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product Not Found"));
    }

    public Product update(Long id, Product newProduct) {
        Product oldProduct = this.findById(id);

        if (productRepository.existsByName(newProduct.getName())) {
            throw new UserAlreadyExistsException("Product Already Exists");
        }

        oldProduct.setName(newProduct.getName());
        oldProduct.setDescription(newProduct.getDescription());
        oldProduct.setPrice(newProduct.getPrice());
        oldProduct.setAvailable(newProduct.getAvailable());

        return productRepository.save(oldProduct);
    }

    public void deleteById(Long id) {
        Product product = this.findById(id);
        productRepository.delete(product);
    }

}
