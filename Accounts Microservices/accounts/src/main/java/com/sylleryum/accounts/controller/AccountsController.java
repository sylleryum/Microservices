package com.sylleryum.accounts.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.sylleryum.accounts.config.AccountsServiceConfig;
import com.sylleryum.accounts.model.*;
import com.sylleryum.accounts.repository.AccountsRepository;
import com.sylleryum.accounts.service.client.CardsFeignClient;
import com.sylleryum.accounts.service.client.LoansFeignClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.micrometer.core.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
public class AccountsController {

    private static final Logger logger = LoggerFactory.getLogger(AccountsController.class);
    private final AccountsRepository accountsRepository;
    private final AccountsServiceConfig accountsConfig;
    private final LoansFeignClient loansFeignClient;
    private final CardsFeignClient cardsFeignClient;

    public AccountsController(AccountsRepository accountsRepository,
                              AccountsServiceConfig accountsConfig,
                              LoansFeignClient loansFeignClient,
                              CardsFeignClient cardsFeignClient) {
        this.accountsRepository = accountsRepository;
        this.accountsConfig = accountsConfig;
        this.loansFeignClient = loansFeignClient;
        this.cardsFeignClient = cardsFeignClient;
    }

    @PostMapping("/myAccount")
    @Timed(value = "getAccountDetails.time", description = "time taken to return Account details")
    public Accounts getAccountDetails(@RequestBody Customer customer) {
        Accounts account = accountsRepository.findByCustomerId(customer.getCustomerId());
        if (account != null){
            return account;
        } else {
            return null;
        }
    }

    @GetMapping("/account/properties")
    public String getPropertyDetails() throws JsonProcessingException {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        Properties properties = new Properties(accountsConfig.getMsg(), accountsConfig.getBuildVersion(),
                accountsConfig.getMailDetails(), accountsConfig.getActiveBranches());
        String jsonStr = ow.writeValueAsString(properties);
        return jsonStr;
    }

    @PostMapping("/myCustomerDetails")
//    @CircuitBreaker(name = "detailsForCustomerSupportApp", fallbackMethod = "myCustomerDetailsFallBack")
    @Retry(name = "retryForCustomerDetails", fallbackMethod = "myCustomerDetailsFallBack")
    public CustomerDetails myCustomerDetails(@RequestHeader("trace-id")String traceId, @RequestBody Customer customer) {
        logger.debug("trace inside accounts: {}",traceId);
        Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId());
        List<Loans> loans = loansFeignClient.getLoansDetails(traceId,customer);
        List<Cards> cards = cardsFeignClient.getCardDetails(traceId,customer);

        CustomerDetails customerDetails = new CustomerDetails();
        customerDetails.setAccounts(accounts);
        customerDetails.setLoans(loans);
        customerDetails.setCards(cards);

        return customerDetails;
    }
    private CustomerDetails myCustomerDetailsFallBack(@RequestHeader("trace-id")String traceId, Customer customer, Throwable t) {
        System.out.println("**fallback");
        Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId());
        List<Loans> loans = loansFeignClient.getLoansDetails(traceId, customer);
        CustomerDetails customerDetails = new CustomerDetails();
        customerDetails.setAccounts(accounts);
        customerDetails.setLoans(loans);
        return customerDetails;

    }

    @GetMapping("/sayHello")
    @RateLimiter(name = "sayHello", fallbackMethod = "sayHelloFallback")
    public String sayHello() {
        System.out.println("Rate limiter AccountsController");
        return "Rate limiter AccountsController";
    }

    private String sayHelloFallback(Throwable t) {
        System.out.println("Rate limiter FALLBACK");
        return "Rate limiter FALLBACK";
    }
}
