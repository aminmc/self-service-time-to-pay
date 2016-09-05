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


import play.libs.F;
import uk.gov.hmrc.ssttp.config.PlayMessageInterpolator;
import uk.gov.hmrc.ssttp.models.Calculation;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.*;

public class CalculationValidator {

    private final Validator validator;

    public CalculationValidator() {
        validator = Validation.byDefaultProvider()
                .configure()
                .messageInterpolator(new PlayMessageInterpolator()).buildValidatorFactory()
                .getValidator();

    }

    public Map<String, List<String>> validate(Calculation calculation) {
        Set<ConstraintViolation<Calculation>> violations = validator.validate(calculation);

        if (!violations.isEmpty()) {
            return violations
                    .stream()
                    .map(v -> new F.Tuple<>(v.getPropertyPath().toString(), v.getMessage()))
                    .collect(groupingBy(t -> t._1, mapping(t -> t._2, toList())));
        }

        return new HashMap<>();
    }
}
