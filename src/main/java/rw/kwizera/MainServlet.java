package rw.kwizera;

import com.google.gson.Gson;
import org.apache.commons.beanutils.BeanUtils;
import rw.kwizera.model.Admin;
import rw.kwizera.model.Guest;
import rw.kwizera.model.RequestDto;
import rw.kwizera.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
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
