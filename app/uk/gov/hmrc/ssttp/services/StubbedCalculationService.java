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
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.hmrc.ssttp.config.PlayMessageInterpolator;
import uk.gov.hmrc.ssttp.models.Calculation;
import uk.gov.hmrc.ssttp.models.PaymentSchedule;
import uk.gov.hmrc.ssttp.models.ValidationException;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

public class StubbedCalculationService implements CalculationService {

    private final static ObjectMapper objectMapper = ObjectMapperFactory.mapper();

    private static Validator validator = Validation.byDefaultProvider()
            .configure()
            .messageInterpolator(new PlayMessageInterpolator()).buildValidatorFactory()
            .getValidator();

    @Override
    public PaymentSchedule generate(Calculation calculation) {
        Set<ConstraintViolation<Calculation>> violations = validator.validate(calculation);

        if (!violations.isEmpty()) {
            List<String> messages = violations
                    .stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(toList());

            throw new ValidationException(messages);
        }
        PaymentSchedule paymentSchedule;
        try {
            paymentSchedule = objectMapper.readValue(this.getClass().getResourceAsStream("/schedule.json"), PaymentSchedule.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return paymentSchedule;

    }
}
