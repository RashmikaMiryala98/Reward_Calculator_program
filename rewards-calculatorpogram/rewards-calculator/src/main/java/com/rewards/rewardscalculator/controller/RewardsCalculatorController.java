package com.rewards.rewardscalculator.controller;

import com.rewards.rewardscalculator.dto.RewardsDto;
import com.rewards.rewardscalculator.dto.Transaction;
import com.rewards.rewardscalculator.model.Customer;
import com.rewards.rewardscalculator.service.RewardsCalculatorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api")
@Validated
public class RewardsCalculatorController {
    @Autowired
    private RewardsCalculatorService rewardsCalculatorService;

    @Operation(summary = "Gets the rewards data as JSON for the given customer",
            description = "Returns the rewards data as JSON for the given customer and for last three months.",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Rewards retrieved successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = RewardsDto.class)))
            })
    @GetMapping("/{customerId}")
    public ResponseEntity<RewardsDto> getRewardsForCustomerId(
            @PathVariable Long customerId,
            @RequestParam(value = "numberOfMonths", required = false) Integer numberOfMonths) {
        RewardsDto rewards = rewardsCalculatorService.calculateRewardsForCustomerId(customerId, numberOfMonths);
        return new ResponseEntity<>(rewards, HttpStatus.OK);
    }

    @Operation(summary = "Creates a transaction entry.",
            description = "Creates a transaction entry with the given input.",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Rewards retrieved successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = RewardsDto.class)))
            })
    @PostMapping("/transaction")
    public ResponseEntity<String> createTransaction(@Valid @RequestBody Transaction transaction) {
        rewardsCalculatorService.createTransaction(transaction);
        return new ResponseEntity<>("success", HttpStatus.OK);
    }

    @Operation(summary = "Creates new Customer",
            description = "Creates a new Customer entry with the given input",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Customer created successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = RewardsDto.class)))
            })
    @PostMapping("/customer")
    public ResponseEntity<Customer> addCustomer(@Valid @RequestBody Customer Customer) {
        Customer customer = rewardsCalculatorService.addCustomer(Customer);
        return new ResponseEntity<>(customer, HttpStatus.OK);
    }


    @Operation(summary = "Calculates Rewards for all customers",
            description = "Calculates Rewards for all customers present in the system",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Calculates Rewards calculated successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Map.class)))
            })

    @GetMapping("/calculate-all")
    public ResponseEntity<Map<Long, RewardsDto>> calculateRewardsForAllCustomers() {
        // Retrieve all customer IDs
        List<Long> customerIds = rewardsCalculatorService.getAllCustomerIds();

        // Calculate rewards for each customer
        Map<Long, RewardsDto> rewardsForAllCustomers = customerIds.stream()
                .collect(Collectors.toMap(
                        customerId -> customerId,
                        customerId -> rewardsCalculatorService.calculateRewardsForCustomerId(customerId, null)
                ));

        return new ResponseEntity<>(rewardsForAllCustomers, HttpStatus.OK);
    }

}
