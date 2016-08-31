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

package uk.gov.hmrc.ssttp.controllers;


import play.data.validation.Constraints;
import play.libs.F;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import uk.gov.hmrc.ssttp.models.Calculation;
import uk.gov.hmrc.ssttp.services.CalculationService;
import uk.gov.hmrc.ssttp.services.StubbedCalculationService;

import static play.libs.Json.fromJson;
import static play.libs.Json.toJson;

public class PaymentCalculationController extends Controller {

    private final static CalculationService calculationService = new StubbedCalculationService();

    @BodyParser.Of(BodyParser.Json.class)
    public static F.Promise<Result> generate() {

        final Calculation calculation = fromJson(request().body().asJson(), Calculation.class);

        return F.Promise.promise(() -> ok(
                toJson(calculationService.generate(calculation))
        ));
    }
}
