package pl.s32832.library.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class UpdateBookRequest {

    @NotBlank
    private String title;

    @Min(1)
    private int totalCopies;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getTotalCopies() { return totalCopies; }
    public void setTotalCopies(int totalCopies) { this.totalCopies = totalCopies; }
}
