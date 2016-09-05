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

import play.libs.F;
import play.mvc.Http;
import play.mvc.Result;
import uk.gov.hmrc.ssttp.models.ValidationException;

import static play.libs.Json.toJson;


public class ValidationAction extends play.mvc.Action.Simple {

    @Override
    public F.Promise<Result> call(Http.Context ctx) throws Throwable {
        try {
            return delegate.call(ctx);
        } catch (ValidationException e) {
            return F.Promise.promise(() -> badRequest(toJson(e.getExceptions())));
        }
    }
}
