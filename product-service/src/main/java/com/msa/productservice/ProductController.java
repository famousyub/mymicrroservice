package com.msa.productservice;

import com.msa.productservice.form.ProductForm;
import com.msa.productservice.response.ResponseProduct;
import com.msa.productservice.response.ResponseProductEnroll;
import com.msa.productservice.response.enums.Code;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/status-check")
    public String status(HttpServletRequest request) {
        return "product-service 작동중 [PORT:" + request.getServerPort() + "]";
    }

    /**
     * 상품 등록
     * @param form 상품 form
     * @return Json Response
     */
    @PostMapping("/products")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseProductEnroll createProduct(@RequestBody ProductForm form) {

        ResponseProduct product = productService.createProduct(form);

        return ResponseProductEnroll.builder()
                .code(Code.SUCCESS)
                .status(HttpStatus.CREATED.value())
                .message("상품 등록 성공")
                .product(product)
                .build();
    }

    /**
     * 전체 상품 보기
     * @return Json Response
     */
    @GetMapping("/products")
    public ResponseEntity findProducts() {
        return ResponseEntity.ok(productService.getProductByAll());
    }

}
