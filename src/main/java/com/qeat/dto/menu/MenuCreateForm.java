package com.qeat.dto.menu;


import com.qeat.domain.Category;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;


@Setter
public class MenuCreateForm{
    @NotBlank
    private String name;

    private String description;

    @NotNull
    @Min(0)
    private Integer price;

    @NotNull
    private Category category;

    private MultipartFile image;
    private String imageUrl;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Integer getPrice() {
        return price;
    }

    public MultipartFile getImage() {
        return image;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Category getCategory() {
        return category;
    }

}

