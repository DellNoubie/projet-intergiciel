package com.ecommerce.order.entity;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderProductQuantity {

    @NotNull(message = "L'id du produit est obligatoire")
    private Integer productId;

    @NotNull
    @Min(value = 1, message = "La quantité doit être au moins 1")
    private Integer quantity;
}
