package pl.s32832.library.dto.request;

import jakarta.validation.constraints.NotNull;

public class ReturnLoanRequest {

    @NotNull
    private Boolean confirm;

    public Boolean getConfirm() { return confirm; }
    public void setConfirm(Boolean confirm) { this.confirm = confirm; }
}
