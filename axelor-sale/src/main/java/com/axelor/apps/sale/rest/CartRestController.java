package com.axelor.apps.sale.rest;

import com.axelor.apps.base.AxelorException;
import com.axelor.apps.sale.db.Cart;
import com.axelor.apps.sale.db.SaleOrder;
import com.axelor.apps.sale.db.repo.CartRepository;
import com.axelor.apps.sale.rest.dto.CartPutRequest;
import com.axelor.apps.sale.rest.dto.SaleOrderResponse;
import com.axelor.apps.sale.service.CartSaleOrderGeneratorService;
import com.axelor.apps.sale.service.CartService;
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
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/aos/cart")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CartRestController {

  @Operation(
      summary = "Empty cart",
      tags = {"Cart"})
  @Path("/empty")
  @PUT
  @HttpExceptionHandler
  public Response emptyCart(CartPutRequest requestBody) {
    RequestValidator.validateBody(requestBody);
    new SecurityCheck().writeAccess(Cart.class, requestBody.getCartId()).check();
    Beans.get(CartService.class).emptyCart(requestBody.fetchCart());

    return ResponseConstructor.build(Response.Status.OK, I18n.get(ITranslation.EMPTY_CART));
  }

  @Operation(
      summary = "Create sale order from cart",
      tags = {"Cart"})
  @Path("/validate/{cartId}")
  @PUT
  @HttpExceptionHandler
  public Response createSaleOrder(@PathParam("cartId") Long cartId)
      throws AxelorException, JsonProcessingException {
    new SecurityCheck().readAccess(Cart.class, cartId).createAccess(SaleOrder.class).check();
    Cart cart = Beans.get(CartRepository.class).find(cartId);
    SaleOrder saleOrder = Beans.get(CartSaleOrderGeneratorService.class).createSaleOrder(cart);
    return ResponseConstructor.buildCreateResponse(saleOrder, new SaleOrderResponse(saleOrder));
  }
}