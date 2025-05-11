    package com.vanvan.musicapp.model;


    import com.vanvan.musicapp.entity.ProductImage;
    import lombok.AllArgsConstructor;
    import lombok.Getter;
    import lombok.NoArgsConstructor;
    import lombok.Setter;

    import java.util.List;

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public class ProductModel {
        private Integer productID;
        private String name;
        private String indoorDimension;
        private Double indoorWeight;
        private String outdoorDimension;
        private Double outdoorWeight;
        private String heatCapacity;
        private String coolingCapacity;
        private Integer numberOfCooling;
        private Double powerComsumption;
        private Double price;
        private String indoorWarranty;
        private String outdoorWarranty;
        private String releaseDate;
        private Integer inventoryQuantity;
        private String radiatorMaterial;
        private String brand;
        private List<ProductImage> productImages;
        private String productStatus;
        private String description;
    }
