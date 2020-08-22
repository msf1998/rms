app.controller("userController",function ($scope,userService) {
    $scope.data = {
        user:{
            username:"",
            password:"",
        },
        password1:"",
        msg:"",
        disabled : true
    }
    $scope.login = function () {
        if($scope.data.user.username != "" && $scope.data.user.password != "") {
            $scope.data.user.password = md5($scope.data.user.password);
            userService.login($scope.data.user).then(function (response) {
                console.log(response);
                if (response.data.code == 1) {
                    window.location = "./index.html"
                }
                alert(response.data.describe)
            })
        } else {
            alert("请输入账号密码")
        }
    }

    $scope.register = function () {
        var username = $scope.data.user.username;
        var pwd = $scope.data.user.password;
        var pwd1 = $scope.data.password1;
        if (username != null && pwd.length > 6 && pwd1 == pwd ) {
            $scope.data.user.password = md5(pwd)
            userService.register($scope.data.user).then(function (response) {
                alert(response.data.describe);
            })
        } else {
            alert("账号或密码不合法");
        }
    }
    $scope.isExists = function () {
        userService.isExists({username:$scope.data.user.username}).then(function (response) {
            if (response.data.code != 1) {
                $scope.data.msg = response.data.describe;
                $scope.data.disabled = true;
            } else {
                $scope.data.msg = "";
                $scope.data.disabled = false;
            }
        })
    }
})