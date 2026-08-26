package com.qeat.dto.menu;

import com.qeat.domain.Category;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class MenuCreateForm {

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
}
