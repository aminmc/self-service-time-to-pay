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

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class StubbedCalculationService implements CalculationService {

    private ObjectMapper objectMapper;

    private CalculationValidator validator;

    public StubbedCalculationService() {
        objectMapper = ObjectMapperFactory.mapper();
        validator = new CalculationValidator();
    }

    @Override
    public PaymentSchedule generate(Calculation calculation) {
        List<String> messages = validator.validate(calculation);

        if (!messages.isEmpty()) {
            throw new ValidationException(messages);
        }

        PaymentSchedule paymentSchedule;
        try {
            paymentSchedule = objectMapper.readValue(this.getClass().getResourceAsStream("/schedule.json"), PaymentSchedule.class);
            calculateInterest(calculation, paymentSchedule);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return paymentSchedule;

    }

    public PaymentSchedule calculateInterest(Calculation calculation, PaymentSchedule paymentSchedule) {
        BigDecimal amountOwed = calculation.getAmountOwed().getAmount();
        BigDecimal numberOfDays = new BigDecimal(calculation.getNumberOfDays());
        BigDecimal interestRate = new BigDecimal(calculation.getInterestRate());

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
}
