package com.sylleryum.loans.repository;
import java.util.List;

import com.sylleryum.loans.model.Loans;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface LoansRepository extends CrudRepository<Loans, Long> {


    List<Loans> findByCustomerIdOrderByStartDtDesc(int customerId);

}