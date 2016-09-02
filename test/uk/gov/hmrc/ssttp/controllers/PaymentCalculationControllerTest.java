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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.junit.BeforeClass;
import org.junit.Test;
import play.libs.Json;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;
import uk.gov.hmrc.ssttp.config.SsttpMicroserviceGlobal;
import uk.gov.hmrc.ssttp.models.PaymentSchedule;
import uk.gov.hmrc.ssttp.services.ObjectMapperFactory;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static play.libs.Json.fromJson;
import static play.test.Helpers.*;


public class PaymentCalculationControllerTest {

    @BeforeClass
    public static void init() throws Exception {
        Json.setObjectMapper(ObjectMapperFactory.mapper());
    }

    @Test
    public void canGenerateAPaymentSchedule() {

        running(testServer(3333, fakeApplication(new SsttpMicroserviceGlobal())), () -> {
            //given:
            JsonNode jsonNode = Json.parse(this.getClass().getResourceAsStream("/calculation.json"));

            //when:
            WSResponse response = WS.url("http://localhost:3333/self-service-time-to-pay/paymentschedule")
                    .post(jsonNode).get(10, TimeUnit.SECONDS);

            //then:
            assertThat(response.getStatus(), is(OK));
            PaymentSchedule paymentSchedule = fromJson(Json.parse(response.getBody()), PaymentSchedule.class);
            assertThat(paymentSchedule, is(notNullValue()));
        });
    }

    @Test
    public void badRequestThrownInvalidCalculation() {

        running(testServer(3333, fakeApplication(new SsttpMicroserviceGlobal())), () -> {
            //given:
            JsonNode jsonNode = Json.parse(this.getClass().getResourceAsStream("/invalid-calculation.json"));

            //when:
            WSResponse response = WS.url("http://localhost:3333/self-service-time-to-pay/paymentschedule")
                    .post(jsonNode).get(10, TimeUnit.SECONDS);

            //then:
            assertThat(response.getStatus(), is(BAD_REQUEST));

            //and: exception contains invalid frequency format
            assertThat(response.asJson().size(), is(1));
        });
    }


}
