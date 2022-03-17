import static spark.Spark.*;
import com.google.gson.Gson;
import dao.DB;
import dao.DeptDaoImplementation;
import dao.NewsDaoImplementation;
import dao.UserDaoImplementation;
import models.Department;
import models.News;
import models.User;
import org.sql2o.Connection;
import org.sql2o.Sql2o;


import java.util.HashMap;
import java.util.Map;

public class App {
    static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567;
    }

    public static void main(String[] args) {

        port(getHerokuAssignedPort());


        DeptDaoImplementation deptDao;
        NewsDaoImplementation newsDao;
        UserDaoImplementation userDao;
        Gson gson = new Gson();
        Connection conn;

        String connectionString = "jdbc:postgresql://gmpaixahqkhzxz:fef77d14054513a9f448c45eb65bdfc007c5074721ed5955c83fecc047bd72b7@ec2-34-192-83-52.compute-1.amazonaws.com:5432/d4moohjoqsa8eg";
        Sql2o sql2o = new Sql2o(connectionString, "gmpaixahqkhzxz", "fef77d14054513a9f448c45eb65bdfc007c5074721ed5955c83fecc047bd72b7");


        deptDao = new DeptDaoImplementation(DB.sql2o);
        userDao = new UserDaoImplementation(DB.sql2o);
        newsDao = new NewsDaoImplementation(DB.sql2o);
        conn = DB.sql2o.open();



        get("/", (request, response) -> {

            Map<String, Object> models = new HashMap<>();
            models.put("Add new department", "/users");
            models.put("View all departments", "/departments");
            return gson.toJson(models);
        });

        get("/departments", "application/json", (request, response) -> gson.toJson(deptDao.allDepartments()));

        post("/departments/new", "application/json", (request, response) -> {
            Department department = gson.fromJson(request.body(), Department.class);
            deptDao.add(department);
            response.status(201);
            return gson.toJson(department);
        });

        get("/departments/:deptId/details", "application/json", (request, response) -> {
            int deptId = Integer.parseInt(request.params("deptId"));
            return gson.toJson(deptDao.findById(deptId));
        });

        post("/departments/:deptId/users/new", "application/json", (request, response) -> {
            int deptId = Integer.parseInt(request.params("deptId"));
            Department department = deptDao.findById(deptId);

            if (department != null) {
                User employee = gson.fromJson(request.body(), User.class);
                employee.setDepartment(department.getName());
                userDao.add(employee);
                deptDao.addUserToDept(department, employee);
                response.status(201);
                return gson.toJson(employee);
            } else {
                throw new ApiException("Department not found", 404);
            }
        });

        get("/departments/:deptId/users", "application/json", (request, response) -> {
            int deptId = Integer.parseInt(request.params("deptId"));
            return gson.toJson(deptDao.allDepartmentEmployees(deptId));
        });


        get("/departments/:deptId/users/:userId/news", "application/json", (request, response) -> {
            int userId = Integer.parseInt(request.params("userId"));
            User foundUser = userDao.findById(userId);

            if (foundUser != null) {
                return gson.toJson(userDao.myNews(userId));
            } else {
                return "{\"Error 404!\":\"User not found\"}";
            }
        });

        post("/departments/:deptId/users/:userId/news/new", "application/json", (request, response) -> {
            int userId = Integer.parseInt(request.params("userId"));
            int deptId = Integer.parseInt(request.params("deptId"));
            User foundUser = userDao.findById(userId);
            Department foundDept = deptDao.findById(deptId);

            if (foundUser != null && foundDept != null) {
                News news = gson.fromJson(request.body(), News.class);
                news.setType(foundDept.getName());
                news.setAuthor(foundUser.getName());
                newsDao.add(news);
                newsDao.addNewsToDepartment(deptId, news.getId(), userId);
                response.status(201);
                return gson.toJson(news);
            } else {
                return "{\"Error 404!\":\"User or Department not found\"}";
            }
        });

        get("/departments/:deptId/news", "application/json", (request, response) -> {
            int deptId = Integer.parseInt(request.params("deptId"));
            return gson.toJson(deptDao.allDepartmentNews(deptId));
        });

//        Users
        get("/users", "application/json", (request, response) -> gson.toJson(userDao.allUsers()));

        get("/users/:userId/details", "application/json", (request, response) -> {
            int userId = Integer.parseInt(request.params("userId"));
            User foundUser = userDao.findById(userId);
            if (foundUser != null) {
                return gson.toJson(userDao.findById(userId));
            } else {
                return "{\"Error 404!\":\"User not found.\"}";
            }
        });

        get("/users/:userId/news", "application/json", (request, response) -> {
            int userId = Integer.parseInt(request.params("userId"));
            return gson.toJson(userDao.myNews(userId));
        });

        post("/users/:userId/news/new", "application/json", (request, response) -> {
            int userId = Integer.parseInt(request.params("userId"));
            User foundUser = userDao.findById(userId);

            if (foundUser != null) {
                News news = gson.fromJson(request.body(), News.class);
                news.setAuthor(foundUser.getName());
                newsDao.add(news);
                newsDao.addNewsToDepartment(0, news.getId(), userId);
                response.status(201);
                return gson.toJson(news);
            } else {
                return "{\"Error 404!\":\"User not found. News cannot be posted without an actual user as the author\"}";
            }
        });


//        News
        get("/news", "application/json", (request, response) -> gson.toJson(newsDao.allNews()));
        get("/news/general", "application/json", (request, response) -> gson.toJson(newsDao.allGeneralNews()));
        get("/news/departments", "application/json", (request, response) -> gson.toJson(newsDao.allDepartmentalNews()));
        get("/news/:newsId/details", "application/json", (request, response) -> {
            int newsId = Integer.parseInt(request.params("newsId"));
            return gson.toJson(newsDao.findById(newsId));
        });



        //filter
        after((req, res) -> res.type("application/json"));
    }
}
