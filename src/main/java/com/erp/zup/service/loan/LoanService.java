package com.erp.zup.service.loan;

import com.erp.zup.api.config.notifiable.NotifiableValidate;
import com.erp.zup.domain.Book;
import com.erp.zup.domain.Loan;
import com.erp.zup.repository.ILoanRepository;
import com.erp.zup.service.email.EmailService;
import jflunt.notifications.Notification;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LoanService extends NotifiableValidate implements ILoanService {


    private final ILoanRepository repository;
    private final EmailService emailService;

    public LoanService(ILoanRepository repository, EmailService emailService) {
        this.repository = repository;
        this.emailService = emailService;
    }


    @Value("${loan.days.default}")
    public String loanDaysDefault;

    @Value("${application.mail.lateLoans.message}")
    public String message;

    @Value("${application.mail.lateLoans.subject}")
    public String subject;

    @Override
    public Loan save(Loan loan) {
        if (repository.existsByBookAndNotReturned(loan.getBook())) {
            addNotification(new Notification("Isbn", "Isbn já cadastrado."));
            return null;
        }
        return repository.save(loan);
    }

    @Override
    public Optional<Loan> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Page<Loan> find(String isbn, String email, Pageable pageable) {
        return repository.findByBookIsbnOrUser(isbn, email, pageable);
    }

    @Override
    public Loan update(Loan loan) {
        return repository.save(loan);
    }

    @Override
    public Page<Loan> getLoansByBook(Book book, Pageable pageable) {
        return repository.findByBook(book, pageable);
    }


    //0 * * ? * ? - 1 EM 1 MINUTE
    //40 27 14 1/1 * ? - 14:27:40
    @Scheduled(cron = "30 30 10 1/1 * ?")
    public void sendEmailToLateLoans() {
        final Integer loanDays = Integer.parseInt(loanDaysDefault);
        LocalDate days = LocalDate.now().minusDays(loanDays);

        List<Loan> loans = repository.findByLoanDateLessThanAndNotReturned(days);
        if (!loans.isEmpty()) {
            List<String> mailList = loans.stream().map(loan -> loan.getUser().getEmail()).collect(Collectors.toList());
            emailService.sendMails(message, subject, mailList);
        }
    }
}
