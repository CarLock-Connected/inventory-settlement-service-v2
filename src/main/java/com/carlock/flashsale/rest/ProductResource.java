package com.carlock.flashsale.rest;

import com.carlock.flashsale.entity.Product;
import com.carlock.flashsale.service.InventoryService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/products")
@Produces(MediaType.APPLICATION_JSON)
public class ProductResource {

    @Inject
    private InventoryService inventoryService;

    @GET
    public Response listProducts() {
        List<Product> products = inventoryService.getAllProducts();
        return Response.ok(products).build();
    }

    @GET
    @Path("/{sku}")
    public Response getProduct(@PathParam("sku") String sku) {
        Product product = inventoryService.getProductBySku(sku);

        if (product == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Product not found\"}")
                    .build();
        }

        return Response.ok(product).build();
    }
}
