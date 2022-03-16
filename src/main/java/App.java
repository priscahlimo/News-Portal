import static spark.Spark.*;
import com.google.gson.Gson;
import dao.DeptDaoImplementation;
import dao.NewsDaoImplementation;
import dao.SQL2oSitemapDao;
import dao.UserDaoImplementation;
import models.Department;
import models.News;
import models.User;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.HashMap;
import java.util.Map;

public class App {

    public static void main(String[] args) {

        DeptDaoImplementation deptDao = new DeptDaoImplementation();
        UserDaoImplementation userDao = new UserDaoImplementation();
        NewsDaoImplementation newsDao = new NewsDaoImplementation();
        SQL2oSitemapDao sitemapDao = new SQL2oSitemapDao();
        Gson gson = new Gson();

        ProcessBuilder process = new ProcessBuilder();
        int port;

        if (process.environment().get("PORT") != null) {
            port = Integer.parseInt(process.environment().get("PORT"));
        } else {
            port = 4567;
        }
        port(port);


        String connectionString = "postgres://bnzylatosfehuz:9342a14425650a095b8371b6519eee9945f452fa3936a2a2c5796e55dce045ed@ec2-3-216-221-31.compute-1.amazonaws.com:5432/d6c8prohuumrv0";
        Sql2o sql2o = new Sql2o(connectionString, "bnzylatosfehuz", "9342a14425650a095b8371b6519eee9945f452fa3936a2a2c5796e55dce045ed\n");

        Connection conn;

        get("/",  (request, response) -> {

            Map<String, Object> models = new HashMap<>();
            models.put("Add new department", "/departments/new");
            models.put("View all departments", "/departments");
            return gson.toJson(models);
        });

        get("/departments","application/json",(request, response) -> gson.toJson(deptDao.allDepartments()));

        post("/departments/new","application/json",(request, response) -> {
            Department department = gson.fromJson(request.body(),Department.class);
            deptDao.add(department);
            response.status(201);
            return gson.toJson(department);
        });

        get("/departments/:deptId/details","application/json",(request, response) -> {
            int deptId = Integer.parseInt(request.params("deptId"));
            return gson.toJson(deptDao.findById(deptId));
        });

        post("/departments/:deptId/users/new","application/json",(request, response) -> {
            int deptId = Integer.parseInt(request.params("deptId"));
            Department department = deptDao.findById(deptId);

            if(department != null){
                User employee = gson.fromJson(request.body(),User.class);
                employee.setDepartment(department.getName());
                userDao.add(employee);
                deptDao.addUserToDept(department,employee);
                response.status(201);
                return gson.toJson(employee);
            } else {
                throw new ApiException("Department not found", 404);
            }
        });

        get("/departments/:deptId/users","application/json",(request, response) -> {
            int deptId = Integer.parseInt(request.params("deptId"));
            return gson.toJson(deptDao.allDepartmentEmployees(deptId));
        });



        get("/departments/:deptId/users/:userId/news","application/json",(request, response) -> {
            int userId = Integer.parseInt(request.params("userId"));
            User foundUser = userDao.findById(userId);

            if (foundUser != null) {
                return gson.toJson(userDao.myNews(userId));
            }
            else {
                return "{\"Error 404!\":\"User not found\"}";
            }
        });

        post("/departments/:deptId/users/:userId/news/new","application/json",(request, response) -> {
            int userId = Integer.parseInt(request.params("userId"));
            int deptId = Integer.parseInt(request.params("deptId"));
            User foundUser = userDao.findById(userId);
            Department foundDept = deptDao.findById(deptId);

            if (foundUser != null && foundDept != null) {
                News news = gson.fromJson(request.body(),News.class);
                news.setType(foundDept.getName());
                news.setAuthor(foundUser.getName());
                newsDao.add(news);
                newsDao.addNewsToDepartment(deptId,news.getId(),userId);
                response.status(201);
                return gson.toJson(news);
            }
            else {
                return "{\"Error 404!\":\"User or Department not found\"}";
            }
        });

        get("/departments/:deptId/news","application/json",(request, response) -> {
            int deptId = Integer.parseInt(request.params("deptId"));
            return gson.toJson(deptDao.allDepartmentNews(deptId));
        });

//        Users
        get("/users","application/json",(request, response) -> gson.toJson(userDao.allUsers()));

        get("/users/:userId/details","application/json",(request, response) -> {
            int userId = Integer.parseInt(request.params("userId"));
            User foundUser = userDao.findById(userId);
            if (foundUser != null) {
                return gson.toJson(userDao.findById(userId));
            }
            else {
                return "{\"Error 404!\":\"User not found.\"}";
            }
        });

        get("/users/:userId/news","application/json",(request, response) -> {
            int userId = Integer.parseInt(request.params("userId"));
            return gson.toJson(userDao.myNews(userId));
        });

        post("/users/:userId/news/new","application/json",(request, response) -> {
            int userId = Integer.parseInt(request.params("userId"));
            User foundUser = userDao.findById(userId);

            if (foundUser != null) {
                News news = gson.fromJson(request.body(),News.class);
                news.setAuthor(foundUser.getName());
                newsDao.add(news);
                newsDao.addNewsToDepartment(0,news.getId(),userId);
                response.status(201);
                return gson.toJson(news);
            }
            else {
                return "{\"Error 404!\":\"User not found. News cannot be posted without an actual user as the author\"}";
            }
        });



//        News
        get("/news","application/json",(request, response) -> gson.toJson(newsDao.allNews()));
        get("/news/general","application/json",(request, response) -> gson.toJson(newsDao.allGeneralNews()));
        get("/news/departments","application/json",(request, response) -> gson.toJson(newsDao.allDepartmentalNews()));
        get("/news/:newsId/details","application/json",(request, response) -> {
            int newsId = Integer.parseInt(request.params("newsId"));
            return gson.toJson(newsDao.findById(newsId));
        });

        get("/sitemap","application/json",(request, response) ->{
            return gson.toJson(sitemapDao.allPaths());
        });



        //filter
        after((req, res) -> res.type("application/json"));
    }
}
