package pl.s32832.library.dto.request;

import jakarta.validation.constraints.NotBlank;

public class UpdateAuthorRequest {

    @NotBlank
    private String name;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
