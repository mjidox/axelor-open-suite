/*
 * Axelor Business Solutions
 *
 * Copyright (C) 2005-2024 Axelor (<http://axelor.com>).
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.axelor.apps.sale.rest;

import com.axelor.apps.base.AxelorException;
import com.axelor.apps.sale.db.SaleOrder;
import com.axelor.apps.sale.db.repo.SaleOrderRepository;
import com.axelor.apps.sale.rest.dto.SaleOrderPostRequest;
import com.axelor.apps.sale.rest.dto.SaleOrderPutRequest;
import com.axelor.apps.sale.rest.dto.SaleOrderResponse;
import com.axelor.apps.sale.service.SaleOrderGeneratorService;
import com.axelor.apps.sale.service.saleorder.SaleOrderConfirmService;
import com.axelor.apps.sale.service.saleorder.SaleOrderFinalizeService;
import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;
import com.axelor.utils.api.HttpExceptionHandler;
import com.axelor.utils.api.RequestValidator;
import com.axelor.utils.api.ResponseConstructor;
import com.axelor.utils.api.SecurityCheck;
import com.axelor.web.ITranslation;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/aos/sale-order")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SaleOrderRestController {
  @Operation(
      summary = "Create a sale oder",
      tags = {"Sale order"})
  @Path("/")
  @POST
  @HttpExceptionHandler
  public Response createSaleOrder(SaleOrderPostRequest requestBody)
      throws AxelorException, JsonProcessingException {
    RequestValidator.validateBody(requestBody);
    new SecurityCheck().createAccess(SaleOrder.class).check();

    SaleOrder saleOrder =
        Beans.get(SaleOrderGeneratorService.class)
            .createSaleOrder(
                requestBody.fetchClientPartner(),
                requestBody.fetchCompany(),
                requestBody.fetchContact(),
                requestBody.fetchCurrency(),
                requestBody.getInAti());

    return ResponseConstructor.buildCreateResponse(saleOrder, new SaleOrderResponse(saleOrder));
  }

  @Operation(
      summary = "Update sale order status",
      tags = {"Sale order"})
  @Path("/status")
  @PUT
  @HttpExceptionHandler
  public Response changeSaleOrderStatus(SaleOrderPutRequest requestBody) throws AxelorException {
    RequestValidator.validateBody(requestBody);
    new SecurityCheck().writeAccess(SaleOrder.class, requestBody.getSaleOrderId()).check();
    Long statusId = requestBody.getStatusId();
    SaleOrder saleOrder = requestBody.fetchSaleOrder();
    if (statusId == SaleOrderRepository.STATUS_FINALIZED_QUOTATION) {
      Beans.get(SaleOrderFinalizeService.class).finalizeQuotation(requestBody.fetchSaleOrder());
    }
    if (statusId == SaleOrderRepository.STATUS_ORDER_CONFIRMED) {
      Beans.get(SaleOrderConfirmService.class)
          .confirmSaleOrder(Beans.get(SaleOrderRepository.class).find(saleOrder.getId()));
    }
    return ResponseConstructor.build(Response.Status.OK, I18n.get(ITranslation.STATUS_CHANGE));
  }
}
