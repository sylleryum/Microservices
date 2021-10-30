package com.sylleryum.loans.controller;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.sylleryum.loans.config.LoansServiceConfig;
import com.sylleryum.loans.model.Customer;
import com.sylleryum.loans.model.Loans;
import com.sylleryum.loans.model.Properties;
import com.sylleryum.loans.repository.LoansRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
public class LoansController {

    private static final Logger logger = LoggerFactory.getLogger(LoansController.class);
    final private LoansRepository loansRepository;
    final private LoansServiceConfig loansConfig;

    public LoansController(LoansRepository loansRepository, LoansServiceConfig loansConfig) {
        this.loansRepository = loansRepository;
        this.loansConfig = loansConfig;
    }

    @PostMapping("/myLoans")
    public List<Loans> getLoansDetails(@RequestHeader("trace-id")String traceId, @RequestBody Customer customer) {
        logger.debug("trace inside loans: {}",traceId);
        System.out.println("******Loans controller");
        List<Loans> loans = loansRepository.findByCustomerIdOrderByStartDtDesc(customer.getCustomerId());

        if (loans != null) {
            return loans;
        } else {
            return null;
        }
    }

    @GetMapping("/loans/properties")
    public String getPropertyDetails() throws JsonProcessingException {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        Properties properties = new Properties(loansConfig.getMsg(), loansConfig.getBuildVersion(),
                loansConfig.getMailDetails(), loansConfig.getActiveBranches());
        String jsonStr = ow.writeValueAsString(properties);
        return jsonStr;
    }

}