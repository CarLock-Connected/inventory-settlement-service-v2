package com.carlock.flashsale.rest;

import com.carlock.flashsale.entity.SaleOrder;
import com.carlock.flashsale.rest.dto.CreateOrderRequest;
import com.carlock.flashsale.service.OrderService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderResource {

    @Inject
    private OrderService orderService;

    @POST
    public Response createOrder(
            CreateOrderRequest request,
            @HeaderParam("Idempotency-Key") String idempotencyKey
    ) {
        if (request == null || request.getCustomerId() == null || request.getSku() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Missing required fields: customerId, sku\"}")
                    .build();
        }

        if (request.getQuantity() <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Quantity must be positive\"}")
                    .build();
        }

        SaleOrder order = orderService.createOrder(
                request.getCustomerId(),
                request.getSku(),
                request.getVariantCode(),
                request.getQuantity(),
                idempotencyKey
        );

        if (order == null) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"error\": \"Order could not be created. Inventory may be insufficient.\"}")
                    .build();
        }

        return Response.status(Response.Status.CREATED).entity(order).build();
    }

    @GET
    @Path("/{orderReference}")
    public Response getOrder(@PathParam("orderReference") String orderReference) {
        SaleOrder order = orderService.findByReference(orderReference);

        if (order == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Order not found\"}")
                    .build();
        }

        return Response.ok(order).build();
    }
}
