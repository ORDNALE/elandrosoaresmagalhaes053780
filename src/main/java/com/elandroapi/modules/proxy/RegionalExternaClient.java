package com.elandroapi.modules.proxy;

import com.elandroapi.modules.dto.response.RegionalExternaResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

@Path("/v1/regionais")
@RegisterRestClient(configKey = "argus-api")
public interface RegionalExternaClient {

    @GET
    List<RegionalExternaResponse> listar();
}
