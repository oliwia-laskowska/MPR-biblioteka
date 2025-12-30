package pl.s32832.library.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExceptionsTest {

    @Test
    void notFoundException_shouldStoreMessage() {
        NotFoundException ex = new NotFoundException("not found");
        assertEquals("not found", ex.getMessage());
    }

    @Test
    void validationException_shouldStoreMessage() {
        ValidationException ex = new ValidationException("validation");
        assertEquals("validation", ex.getMessage());
    }

    @Test
    void businessRuleException_shouldStoreMessage() {
        BusinessRuleException ex = new BusinessRuleException("rule");
        assertEquals("rule", ex.getMessage());
    }
}
