package rw.kwizera;

import com.google.gson.Gson;
import org.apache.commons.beanutils.BeanUtils;
import rw.kwizera.model.*;
import rw.kwizera.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.stream.Collectors;

public class MainServlet extends HttpServlet {
    private UserService userService;
    private Gson gson = new Gson();

    public MainServlet() {
        this.userService = new UserService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String jsonResponse = gson.toJson(userService.findAllUsers());

        this.outputResponse(resp, jsonResponse, 200);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String reqBody = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        setAccessControlHeaders(resp);
        String action = req.getParameter("ACTION");
        if (action != null && action.equalsIgnoreCase("login")){
            this.login(reqBody, resp);
        } else this.register(reqBody, resp);
    }

    private void login(String reqBody ,HttpServletResponse resp){
        Gson gson = new Gson();
        LoginDto loginBody =gson.fromJson(reqBody, LoginDto.class);

        User userLoggedIn = userService.login(loginBody);

        int rc = HttpServletResponse.SC_OK;
        if(userLoggedIn == null)
            this.outputResponse(resp, "username or password not correct", HttpServletResponse.SC_BAD_REQUEST);
        else this.outputResponse(resp, gson.toJson(userLoggedIn), 200);
    }

    private void register(String reqBody ,HttpServletResponse resp) {
        int rc = HttpServletResponse.SC_OK;
        RequestDto requestBody = gson.fromJson(reqBody, RequestDto.class);
        try {
            if (requestBody.getRole().equalsIgnoreCase("admin")){
                Admin request = new Admin();
                BeanUtils.copyProperties(request, requestBody);
                Admin addedAdmin = userService.addAdmin(request);
                if(addedAdmin == null)
                    this.outputResponse(resp, "password rules not met", HttpServletResponse.SC_BAD_REQUEST);
                else this.outputResponse(resp, gson.toJson(addedAdmin), rc);
            } else if (requestBody.getRole().equalsIgnoreCase("guest")){
                Guest request = new Guest();
                BeanUtils.copyProperties(request, requestBody);
                Guest addedGuest = userService.addGuest(request);
                if(addedGuest == null)
                    this.outputResponse(resp, "password rules not met", HttpServletResponse.SC_BAD_REQUEST);
                else this.outputResponse(resp, gson.toJson(addedGuest), rc);
            } else {
                rc = HttpServletResponse.SC_BAD_REQUEST;
                this.outputResponse(resp, "please chose a role", rc);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            rc = HttpServletResponse.SC_BAD_REQUEST;
            this.outputResponse(resp, "please chose a role", rc);
        }
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        setAccessControlHeaders(resp);
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    private void setAccessControlHeaders(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "*");
        resp.setHeader("Access-Control-Allow-Headers","*");
    }

    private void outputResponse(HttpServletResponse response, String payload, int status) {
        response.setHeader("Content-Type", "application/json");
        try {
            response.setStatus(status);
            if (payload != null) {
                OutputStream outputStream = response.getOutputStream();
                outputStream.write(payload.getBytes());
                outputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
