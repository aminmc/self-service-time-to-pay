/*
 * Copyright 2016 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.ssttp.services;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

import uk.gov.hmrc.ssttp.models.Amount;
import uk.gov.hmrc.ssttp.models.Calculation;
import uk.gov.hmrc.ssttp.models.PaymentSchedule;
import uk.gov.hmrc.ssttp.models.ValidationException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.UUID.randomUUID;

public class StubbedCalculationService implements CalculationService {

    private ObjectMapper objectMapper;

    private CalculationValidator validator;

    public StubbedCalculationService() {
        objectMapper = ObjectMapperFactory.mapper();
        validator = new CalculationValidator();
    }

    @Override
    public PaymentSchedule generate(Calculation calculation) {
        Map<String, List<String>> validationErrors = validator.validate(calculation);
        if (!validationErrors.isEmpty()) {
            throw new ValidationException(validationErrors);
        }
        PaymentSchedule paymentSchedule = new PaymentSchedule();
        paymentSchedule.setId(randomUUID().toString());
        paymentSchedule.setCreatedOn(LocalDate.now());
        calculateInterest(calculation, paymentSchedule);
        return paymentSchedule;

    }

    //TODO: Possibly move this logic to a new class and avoid the stub
    private PaymentSchedule calculateInterest(Calculation calculation, PaymentSchedule paymentSchedule) {
        BigDecimal amountOwed = calculation.getAmountOwed().getAmount();
        BigDecimal numberOfDays = calculateNumberOfDays(calculation);
        BigDecimal interestRate = new BigDecimal(calculation.getInterestRate() == null ? 3.5 : calculation.getInterestRate());

        BigDecimal calculatedInterest = ((amountOwed
                .multiply(interestRate)
                .multiply(numberOfDays))
                .divide(new BigDecimal(36600), BigDecimal.ROUND_FLOOR))
                .setScale(2, BigDecimal.ROUND_FLOOR);


        Amount interestCharged = new Amount();
        interestCharged.setAmount(calculatedInterest);
        interestCharged.setCurrency("GBP");
        paymentSchedule.setTotalInterestCharged(interestCharged);

        return paymentSchedule;
    }

    private BigDecimal calculateNumberOfDays(Calculation calculation) {
        LocalDate startDate = LocalDate.now();
        return new BigDecimal(DAYS.between(startDate, calculation.getWhenDue()));
    }
}
