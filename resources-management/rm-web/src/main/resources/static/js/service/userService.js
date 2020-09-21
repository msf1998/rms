app.service("userService",function ($http) {
    this.login = function (user) {
        return $http.post("/manage/api/user/login",user);
    }
    this.register = function (user) {
        return $http.post("/manage/api/user/register",user);
    }
    this.isExists = function (user) {
        return $http.post("/manage/api/user/check",user);
    }
})