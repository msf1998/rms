app.controller("fileController",function ($scope,$compile,fileService) {
    $scope.data = {
        root:{},
        newName:""
    }
    var id = "a";
    fileService.listFiles().then(function (response) {
        if (response.data.code == 1) {
            $scope.data.root = response.data.obj;
            if ($scope.data.root.directory) {
                $("#a").append(show($scope.data.root))
            }
        } else if(response.data.code == 3){
            window.location = "/manage/login.html"
        } else{
            alert(response.data.describe);
        }
    })

    function show(root) {
        var html = "<ul class='list-group' style='margin-top: 10px' hidden='hidden'>"
        for (var i = 0; i < root.list.length; i ++) {
            id += "a";
            html += "<li id='" + id + "' class='list-group-item' data-path='" + root.list[i].path + "' data-directory='" + root.list[i].directory + "'>"
            html += root.list[i].directory ? "<span class='badge'>文件夹</span>" +
                "<span class='name' style='color: blue'>" + root.list[i].name + "</span>" : "<span class='name'>" + root.list[i].name + "</span>";
            if (root.list[i].directory) {
                html += show(root.list[i]);
            }
            html += "</li>"
        }
        html += "</ul>"
        return html;
    }

    $scope.hidden = function (event) {
        var childs = event.target.children;
        for (var i = 2; i < childs.length; i ++) {
            childs[i].getAttribute("hidden") == null ? childs[i].setAttribute("hidden","hidden") : childs[i].removeAttribute("hidden");
        }
    }

    $scope.deleteDirectory = function () {
        if (confirm("确定删除该文件？")) {
            let path = $("#menu1").attr("data-path");
            let id = $("#menu1").attr("data-id");
            fileService.deleteFile({"path":path}).then(function (response) {
                if (response.data.code == 1) {
                    $("#"+id).remove();
                } else if (response.data.code == 3) {
                    window.location = "/manage/login.html"
                } else {
                    alert(response.data.describe);
                }
            })
        }
    }
    $scope.deleteDocument = function () {
        if (confirm("确定删除该文件？")) {
            let path = $("#menu2").attr("data-path");
            let id = $("#menu2").attr("data-id");
            fileService.deleteFile({path:path}).then(function (response) {
                if (response.data.code == 1) {
                    $("#"+id).remove();
                } else if (response.data.code == 2) {
                    window.location = "/manage/login.html"
                } else {
                    alert(response.data.describe);
                }
            })
        }
    }
    $scope.renameDirectory = function () {
        let path = $("#menu1").attr("data-path");
        let id = $("#menu1").attr("data-id");
        fileService.rename({path:path,name:$scope.data.newName}).then(function (response) {
            if (response.data.code == 1) {
                let split = path.split("\\");
                path = "";
                for (var i = 0; i < split.length - 1; i ++) {
                    path += (split[i] + "\\");
                }
                path += $scope.data.newName
                $("#" + id).attr("data-path",path)
                $("#" + id + ">.name").text($scope.data.newName)
                alert("重命名成功")
            } else if (response.data.code == 3) {
                window.location = "/manage/login.html"
            } else {
                alert(response.data.describe);
            }
        })
    }
    $scope.renameDocument = function () {
        let path = $("#menu2").attr("data-path");
        let id = $("#menu2").attr("data-id");
        fileService.rename({path:path,name:$scope.data.newName}).then(function (response) {
            if (response.data.code == 1) {
                let split = path.split("\\");
                path = "";
                for (var i = 0; i < split.length - 1; i ++) {
                    path += split[i];
                }
                path += $scope.data.newName
                $("#" + id).attr("data-path",path)
                $("#" + id + ">.name").text($scope.data.newName)
                alert("重命名成功")
            } else if (response.data.code == 3) {
                window.location = "/manage/login.html"
            } else {
                alert(response.data.describe);
            }
        })
    }
    $scope.createDirectory = function () {
        let path = $("#menu1").attr("data-path");
        let id1 = $("#menu1").attr("data-id");
        fileService.createDirectory({path:path,name:$scope.data.newName}).then(function (response) {
            if (response.data.code == 1) {
                id += "a"
                var html = "<li id='" + id +"' class='list-group-item' data-directory='true' data-path='" + path + "/" + $scope.data.newName + "'>"
                html += "<span class='badge'>文件夹</span>"
                html += "<span class='name' style='color: blue'>" +$scope.data.newName + "</span>"
                html +="<ul class='list-group' hidden='hidden'></ul>"
                html += "</li>"
                $("#" + id1 + ">ul").append(html)
                alert("创建成功")
            } else if (response.data.code == 3) {
                window.location = "/manage/login.html"
            } else {
                alert(response.data.describe);
            }
        })
    }
    $scope.createDocument = function () {
        var form = new FormData();
        var file = $("#leading-in-document")[0].files[0];
        if (file.size < 10 * 1024 * 1024) {
            form.append("file", file);
            var path = $("#menu1").attr("data-path");
            var id1 = $("#menu1").attr("data-id");
            form.append("path",path);
            fileService.createDocument(form).then(function (response) {
                if (response.data.code == 1) {
                    id += "a"
                    var html = "<li id='" + id +"' class='list-group-item' data-directory='false' data-path='" + path + "\\" + file.name + "'>"
                    html += "<span class='name'>" + file.name + "</span>"
                    html += "</li>"
                    $("#" + id1 + ">ul").append(html)
                    alert("导入成功")
                } else if (response.data.code == 3) {
                    window.location = "/manage/login.html"
                } else {
                    alert(response.data.describe);
                }
            })
        } else {
            alert("文件过大")
        }
    }
})