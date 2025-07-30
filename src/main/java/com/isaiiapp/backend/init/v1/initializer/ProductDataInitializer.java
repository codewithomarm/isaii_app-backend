package com.isaiiapp.backend.init.v1.initializer;

import com.isaiiapp.backend.product.v1.category.model.Category;
import com.isaiiapp.backend.product.v1.category.repository.CategoryRepository;
import com.isaiiapp.backend.product.v1.product.model.Product;
import com.isaiiapp.backend.product.v1.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@Order(3)
@RequiredArgsConstructor
@Slf4j
public class ProductDataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (productRepository.count() > 0) {
            log.info("Productos ya existen, se omite la inserción.");
            return;
        }

        log.info("Inicializando categorías y productos para pizzería...");

        // Categorías
        Category pizzas = createCategory("Pizzas", "Pizzas artesanales al horno de leña");
        Category pastas = createCategory("Pastas", "Clásicos italianos con salsas caseras");
        Category bebidas = createCategory("Bebidas", "Refrescos, jugos y bebidas italianas");
        Category postres = createCategory("Postres", "Delicias dulces al estilo italiano");

        // Productos - Pizzas
        createProduct("Pizza Margherita", pizzas, new BigDecimal("8.50"), "Tomate, mozzarella y albahaca fresca");
        createProduct("Pizza Pepperoni", pizzas, new BigDecimal("9.50"), "Mozzarella y pepperoni al horno");
        createProduct("Pizza Cuatro Quesos", pizzas, new BigDecimal("10.00"), "Mozzarella, gorgonzola, parmesano y provolone");

        // Productos - Pastas
        createProduct("Spaghetti Bolognese", pastas, new BigDecimal("7.50"), "Salsa boloñesa con carne molida");
        createProduct("Fettuccine Alfredo", pastas, new BigDecimal("7.80"), "Salsa cremosa de parmesano y mantequilla");
        createProduct("Lasagna Clásica", pastas, new BigDecimal("8.20"), "Capas de pasta, carne, bechamel y queso");

        // Productos - Bebidas
        createProduct("Limonada Italiana", bebidas, new BigDecimal("2.50"), "Refrescante con un toque de menta");
        createProduct("Agua con gas San Pellegrino", bebidas, new BigDecimal("3.00"), "Botella 500ml");
        createProduct("Coca-Cola", bebidas, new BigDecimal("2.00"), "Lata 355ml");

        // Productos - Postres
        createProduct("Tiramisú", postres, new BigDecimal("4.50"), "Postre frío de café, crema y cacao");
        createProduct("Panna Cotta", postres, new BigDecimal("4.00"), "Flan italiano con coulis de frutas rojas");

        log.info("Productos de pizzería inicializados correctamente.");
    }

    private Category createCategory(String name, String description) {
        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        category.setIsActive(true);
        return categoryRepository.save(category);
    }

    private void createProduct(String name, Category category, BigDecimal price, String description) {
        Product product = new Product();
        product.setName(name);
        product.setCategory(category);
        product.setPrice(price);
        product.setDescription(description);
        product.setIsActive(true);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        productRepository.save(product);
    }
}
