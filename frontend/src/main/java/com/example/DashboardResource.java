package com.example;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * ダッシュボード画面を提供するリソースクラス
 */
@Path("/")
public class DashboardResource {
    
    @Inject
    Template dashboard;
    
    /**
     * ダッシュボード画面を表示
     */
    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance getDashboard() {
        return dashboard.instance();
    }
}
