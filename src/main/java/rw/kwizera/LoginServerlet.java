package rw.kwizera;

import com.google.gson.Gson;
import rw.kwizera.model.LoginDto;
import rw.kwizera.model.User;
import rw.kwizera.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.stream.Collectors;

public class LoginServerlet extends HttpServlet {
    private UserService userService;

    public LoginServerlet() {
        this.userService = new UserService();
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.outputResponse(resp, "jsonResponse", 200);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String reqBody = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        Gson gson = new Gson();
        LoginDto loginBody =gson.fromJson(reqBody, LoginDto.class);

        User userLoggedIn = userService.login(loginBody);

        int rc = HttpServletResponse.SC_OK;
        if(userLoggedIn == null)
            this.outputResponse(resp, "username or password not correct", HttpServletResponse.SC_BAD_REQUEST);
        else this.outputResponse(resp, gson.toJson(userLoggedIn), 200);
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
