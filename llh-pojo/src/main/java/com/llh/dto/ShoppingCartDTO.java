package com.llh.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.io.Serializable;

@Data
public class ShoppingCartDTO implements Serializable {

    @JsonProperty("dishId")
    private Long productId;
    @JsonProperty("setmealId")
    private Long comboId;
    @JsonProperty("dishFlavor")
    private String productSpec;

}
