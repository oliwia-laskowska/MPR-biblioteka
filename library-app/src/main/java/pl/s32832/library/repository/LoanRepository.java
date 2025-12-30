package pl.s32832.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.s32832.library.entity.Loan;

public interface LoanRepository extends JpaRepository<Loan, Long> {
}
