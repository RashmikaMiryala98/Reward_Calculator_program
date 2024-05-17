package com.rewards.rewardscalculator.service;


import com.rewards.rewardscalculator.dto.RewardsDto;
import com.rewards.rewardscalculator.dto.Transaction;
import com.rewards.rewardscalculator.exception.RewardsApplicationBadRequestGenericError;
import com.rewards.rewardscalculator.model.Customer;
import com.rewards.rewardscalculator.model.CustomerTransaction;
import com.rewards.rewardscalculator.repository.CustomerRepository;
import com.rewards.rewardscalculator.repository.CustomerTransactionRepository;
import com.rewards.rewardscalculator.util.RewardCalculatorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RewardsCalculatorService {


    @Autowired
    private CustomerTransactionRepository transactionRepository;
    @Autowired
    private CustomerRepository customerRepository;

    @Value("${rewards.calculation.months}")
    private int rewardsCalculationNumMonths;


    public Customer addCustomer(Customer customer) {
        try {
            return customerRepository.save(customer);
        } catch (DataIntegrityViolationException ex) {
            throw new RewardsApplicationBadRequestGenericError("Customer name already exists: " + customer.getCustomerName());
        }
    }

    public RewardsDto calculateRewardsForCustomerId(Long customerId, Integer numMonths) {

        if (!customerRepository.existsById(customerId)) {
            throw new RewardsApplicationBadRequestGenericError("Customer with ID " + customerId + " not found");
        }


        if (numMonths == null || numMonths <= 0) {
            numMonths = rewardsCalculationNumMonths;
        }
        LocalDateTime now = LocalDateTime.now();
        RewardCalculatorUtil rewardCalculatorUtil = new RewardCalculatorUtil();
        Map<String, Long> monthlyRewards = new HashMap<>();
        Long totalRewardsPoints = 0L;

        for (int i = 0; i < numMonths; i++) {
            LocalDateTime targetMonth = now.minusMonths(i);
            List<CustomerTransaction> transactions = transactionRepository.findAllByTransactionDate(customerId, targetMonth.getYear(), targetMonth.getMonthValue());
            Long monthlyRewardPoints = rewardCalculatorUtil.calculateRewards(transactions);
            totalRewardsPoints += monthlyRewardPoints;
            monthlyRewards.put(targetMonth.getMonth().name(), monthlyRewardPoints);
        }

        return new RewardsDto(customerId, monthlyRewards, totalRewardsPoints);
    }

    public void createTransaction(Transaction transaction) {
        LocalDateTime now = LocalDateTime.now();
        Timestamp timestamp = Timestamp.valueOf(now);

        // Retrieve the customer by ID
        Customer customer = customerRepository.findById(transaction.getCustomerId())
                .orElseThrow(() -> new RewardsApplicationBadRequestGenericError("Customer not found"));

        CustomerTransaction customerTransaction = new CustomerTransaction();
        customerTransaction.setCustomer(customer);
        customerTransaction.setTransactionAmount(transaction.getTransactionAmount());
        customerTransaction.setTransactionDate(timestamp);
        transactionRepository.save(customerTransaction);
    }

    public List<Long> getAllCustomerIds() {
        return customerRepository.getAllCustomerIds();
    }
}
