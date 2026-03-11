package com.carlock.flashsale.rest;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/api")
public class JaxRsApplication extends Application {
    // JAX-RS will auto-discover resource classes
}
