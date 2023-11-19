package com.msa.productservice;

import com.msa.productservice.form.ProductForm;
import com.msa.productservice.response.ResponseProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * 전체 상품 찾기
     * @return ResponseProduct
     */
    public List<ResponseProduct> getProductByAll() {
        return productRepository.findAll()
                .stream()
                .map(product -> ResponseProduct.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .stock(product.getStock())
                        .price(product.getPrice())
                        .createdAt(product.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 상품 등록
     * @param form 상품 form
     * @return ResponseProduct
     */
    public ResponseProduct createProduct(ProductForm form) {

        Product product = Product.builder()
                    .name(form.getName())
                    .stock(form.getStock())
                    .price(form.getPrice())
                    .build();

        Product savedProduct = productRepository.save(product);

        return ResponseProduct.builder()
                .id(savedProduct.getId())
                .name(savedProduct.getName())
                .stock(savedProduct.getStock())
                .price(savedProduct.getPrice())
                .build();
    }

}
